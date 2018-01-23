package lol.clann.object.nbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import static lol.clann.Utils.ReflectionUtils.getRefClass;
import lol.clann.object.Refection.MethodCondition;
import lol.clann.object.Refection.RefClass;
import lol.clann.object.Refection.RefMethod;
import static lol.clann.object.nbt.utils.NBTUtils.nbtUtils;


/**
 * Created by DPOH-VAR on 23.01.14
 */
public final class NBTBlockUtils {

    /**
     * static access to utils
     */
    public static final NBTBlockUtils nbtBlockUtils = new NBTBlockUtils();

    private NBTBlockUtils(){}

    public RefClass classCraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");
    public RefClass classTileEntity = getRefClass("{nms}.TileEntity, {nm}.tileentity.TileEntity, {TileEntity}");
    public RefMethod getTileEntityAt = classCraftWorld.findMethodByReturnType(classTileEntity); // (int x, int y, int z)
    public RefMethod getUpdatePacket = classTileEntity.findMethodByReturnType(
            "{nms}.PacketPlayOutTileEntityData",
            "{nms}.Packet, {nm}.network.Packet {nm}.network.packet.Packet, {Packet}"
    );
    public RefMethod read = classTileEntity.findMethod(
            new MethodCondition()
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("b"),
            new MethodCondition()
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("save")
    );
    public RefMethod write = classTileEntity.findMethod( new MethodCondition()
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("a")
    );

    /**
     * read NBTTagCompound for block
     * @param block bukkit block
     * @param compound empty compound to read
     */
    public void readTag(Block block, Object compound){
        Object tile = getTileEntity(block);
        if (tile!=null) read.of(tile).call(compound);
    }

    /**
     * set NBTTagCompound to block. Watch for x, y, z
     * @param block bukkit block
     * @param compound NBTTagCompound
     */
    public void setTag(Block block, Object compound){
        compound = nbtUtils.cloneTag(compound);
        Map<String, Object> map = nbtUtils.getHandleMap(compound);
        map.put("x", nbtUtils.createTagInt(block.getX()));
        map.put("y", nbtUtils.createTagInt(block.getY()));
        map.put("z", nbtUtils.createTagInt(block.getZ()));
        setTagUnsafe(block, compound);
    }

    /**
     * set NBTTagCompound to block
     * @param block bukkit block
     * @param compound NBTTagCompound
     */
    public void setTagUnsafe(Block block, Object compound){
        Object tile = getTileEntity(block);
        if (tile != null) write.of(tile).call(compound);
    }

    /**
     * send update packet to all nearby players
     * @param block bukkit block
     */
    public void update(Block block){
        if (block == null) return;
        Object tile = getTileEntity(block);
        if (tile == null) return;
        Object packet = getUpdatePacket.of(tile).call();
        if (packet == null) return;
        int maxDist = Bukkit.getServer().getViewDistance() * 32;
        for (Player p : block.getWorld().getPlayers()) {
            if (p.getLocation().distance(block.getLocation()) < maxDist) {
                PacketUtils.packetUtils.sendPacket(p, packet);
            }
        }
    }

    /**
     * Get tile entity at block coordinates
     * @param block bukkit block
     * @return tile entity
     */
    public Object getTileEntity(Block block){
        return getTileEntityAt.of(block.getWorld()).call(block.getX(), block.getY(), block.getZ());
    }


}
