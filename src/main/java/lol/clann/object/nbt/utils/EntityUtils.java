package lol.clann.object.nbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import static lol.clann.Utils.ReflectionUtils.getRefClass;
import static lol.clann.Utils.ReflectionUtils.isForge;
import lol.clann.object.Refection.MethodCondition;
import lol.clann.object.Refection.RefClass;
import lol.clann.object.Refection.RefField;
import lol.clann.object.Refection.RefMethod;
import static lol.clann.object.nbt.utils.NBTUtils.nbtUtils;

public class EntityUtils {

    public static EntityUtils entityUtils = new EntityUtils();

    public RefClass cEntity = getRefClass("{nms}.Entity, {nm}.entity.Entity, {Entity}");
    public RefClass cCraftEntity = getRefClass("{cb}.entity.CraftEntity, {CraftEntity}");
    public RefClass cEntityPlayer = getRefClass("{nms}.EntityPlayer, {nm}.entity.player.EntityPlayer, {EntityPlayer}");
    public RefMethod mGetHandleEntity = cCraftEntity.findMethodByReturnType(cEntity);
    public RefMethod mReadEntity;
    public RefMethod mWriteEntity;
    public RefMethod mReadPlayer;
    public RefMethod mWritePlayer;
    public RefField fForgeData;

    public RefMethod mCreateEntity;
    public RefMethod mGetWorldHandle;
    public RefMethod mGetBukkitEntity;
    public RefMethod mAddEntityToWorld;

    private EntityUtils() {
        RefClass cCraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");
        RefClass cWorldServer = getRefClass("{nms}.WorldServer, {nm}.world.WorldServer, {WorldServer}");
        RefClass cWorld = getRefClass("{nms}.World, {nm}.world.World, {World}");
        RefClass cNBTTagCompound = getRefClass("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");

        try {
            RefClass cEntityTypes = getRefClass("{nms}.EntityTypes, {nm}.entity.EntityTypes, {nm}.entity.EntityList, {EntityTypes}");
            mCreateEntity = cEntityTypes.findMethodByParams(cNBTTagCompound, cWorld);
            mGetWorldHandle = cCraftWorld.findMethodByReturnType(cWorldServer);
            mGetBukkitEntity = cEntity.findMethodByReturnType(cCraftEntity);
            mAddEntityToWorld = cWorld.findMethod(
                    new MethodCondition()
                    .withReturnType(boolean.class)
                    .withName("addEntity")
                    .withTypes(cEntity, CreatureSpawnEvent.SpawnReason.class)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isForge()) {
            try {
                fForgeData = cEntity.findField(cNBTTagCompound);
            } catch (Exception ignored) {
            }
            try { // forge 1.6+
                mWriteEntity = mWritePlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("e")
                );
                mReadEntity = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("c").withIndex(0)
                );
                mReadPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("d")
                );
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Unknown version of forge", e);
                throw new RuntimeException(e);
            }
        } else {
            try { // bukkit 1.6+
                mWriteEntity = mWritePlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("f")
                );
                mReadEntity = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("c")
                );
                mReadPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("e")
                );
            } catch (Exception ignored) { // old bukkit
                mWriteEntity = mWritePlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("e")
                );
                mReadEntity = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("c")
                );
                mReadPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("d")
                );
            }
        }
    }

    public Object getHandleEntity(Entity entity) {
        return mGetHandleEntity.of(entity).call();
    }

    public void readEntity(Entity entity, Object nbtTagCompound) {
        Object nmsEntity = getHandleEntity(entity);
        if (cEntityPlayer.isInstance(nmsEntity)) {
            mReadPlayer.of(nmsEntity).call(nbtTagCompound);
        } else {
            mReadEntity.of(nmsEntity).call(nbtTagCompound);
        }
    }

    public void writeEntity(Entity entity, Object nbtTagCompound) {
        Object liv = getHandleEntity(entity);
        if (entity.getType() == EntityType.PLAYER) {
            mWritePlayer.of(liv).call(nbtTagCompound);
        } else {
            mWriteEntity.of(liv).call(nbtTagCompound);
        }
    }

    public Object getForgeData(Entity entity) {
        if (fForgeData == null) {
            return null;
        }
        Object nmsEntity = getHandleEntity(entity);
        return fForgeData.of(nmsEntity).get();
    }

    public void setForgeData(Entity entity, Object nbtTagCompound) {
        if (fForgeData == null) {
            return;
        }
        Object nmsEntity = getHandleEntity(entity);
        if (nbtTagCompound != null) {
            nbtUtils.cloneTag(nbtTagCompound);
        }
        fForgeData.of(nmsEntity).set(nbtTagCompound);
    }

    public Entity spawnEntity(Object nbtTagCompound, World world) {
        nbtTagCompound = nbtUtils.cloneTag(nbtTagCompound);
        Object nmsWorld = mGetWorldHandle.of(world).call();
        Object nmsEntity = mCreateEntity.call(nbtTagCompound, nmsWorld);
        if (nmsEntity == null) {
            return null;
        }
        mAddEntityToWorld.of(nmsWorld).call(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Entity entity = (Entity) mGetBukkitEntity.of(nmsEntity).call();

        Location loc = entity.getLocation();
        Object tagPos = nbtUtils.createTagList();
        nbtUtils.setNBTTagListType(tagPos, (byte) 6);
        List<Object> handleList = nbtUtils.getHandleList(tagPos);
        handleList.add(nbtUtils.createTagDouble(loc.getX()));
        handleList.add(nbtUtils.createTagDouble(loc.getY()));
        handleList.add(nbtUtils.createTagDouble(loc.getZ()));

        Entity currentEntity = entity;
        while (true) {
            Map<String, Object> handleMap = nbtUtils.getHandleMap(nbtTagCompound);
            nbtTagCompound = handleMap.get("Riding");
            if (nbtTagCompound == null) {
                break;
            }
            Map<String, Object> ridingMap = nbtUtils.getHandleMap(nbtTagCompound);
            ridingMap.put("Pos", nbtUtils.cloneTag(tagPos));
            Object nmsRiding = mCreateEntity.call(nbtTagCompound, nmsWorld);
            if (nmsRiding == null) {
                break;
            }
            mAddEntityToWorld.of(nmsWorld).call(nmsRiding);
            Entity riding = (Entity) mGetBukkitEntity.of(nmsRiding).call();
            riding.setPassenger(currentEntity);
            currentEntity = riding;
        }
        return entity;
    }
}
