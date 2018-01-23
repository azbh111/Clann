/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.*;
import java.util.*;
import lol.clann.*;
import lol.clann.Utils.ObscureUtil;
import static lol.clann.Utils.ReflectionUtils.*;
import lol.clann.exception.*;
import lol.clann.object.Refection.MethodCondition;
import lol.clann.object.Refection.RefClass;
import lol.clann.object.Refection.RefConstructor;
import lol.clann.object.Refection.RefField;
import lol.clann.object.Refection.RefMethod;
import lol.clann.object.nbt.NBTTagCompound;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.enchantments.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

/**
 *
 * @author Administrator
 */
public class ReflectApi {

    public static RefClass ItemInWorldManager = getRefClass("{nms}.ItemInWorldManager, {nm}.server.management.ItemInWorldManager, {ItemInWorldManager}, {nms}.PlayerInteractManager, {nm}.server.management.PlayerInteractManager, {PlayerInteractManager}");
    public static RefClass WorldServer = getRefClass("{nms}.WorldServer, {nm}.world.WorldServer, {WorldServer}");
    public static RefClass MinecraftServer = getRefClass("{nms}.MinecraftServer, {nm}.server.MinecraftServer, {MinecraftServer}");
    public static RefClass CraftServer = getRefClass("{cb}.CraftServer, {CraftServer}");

    public static RefClass JsonToNBT = getRefClass("{nms}.JsonToNBT, {nm}.nbt.JsonToNBT, {JsonToNBT}");
    public static RefClass NBTBase = getRefClass("{nms}.NBTBase, {nm}.nbt.NBTBase, {NBTBase}");
    public static RefClass NBTTagByte = getRefClass("{nms}.NBTTagByte, {nm}.nbt.NBTTagByte, {NBTTagByte}");
    public static RefClass NBTTagShort = getRefClass("{nms}.NBTTagShort, {nm}.nbt.NBTTagShort, {NBTTagShort}");
    public static RefClass NBTTagInt = getRefClass("{nms}.NBTTagInt, {nm}.nbt.NBTTagInt, {NBTTagInt}");
    public static RefClass NBTTagLong = getRefClass("{nms}.NBTTagLong, {nm}.nbt.NBTTagLong, {NBTTagLong}");
    public static RefClass NBTTagFloat = getRefClass("{nms}.NBTTagFloat, {nm}.nbt.NBTTagFloat, {NBTTagFloat}");
    public static RefClass NBTTagDouble = getRefClass("{nms}.NBTTagDouble, {nm}.nbt.NBTTagDouble, {NBTTagDouble}");
    public static RefClass NBTTagString = getRefClass("{nms}.NBTTagString, {nm}.nbt.NBTTagString, {NBTTagString}");
    public static RefClass NBTTagByteArray = getRefClass("{nms}.NBTTagByteArray, {nm}.nbt.NBTTagByteArray, {NBTTagByteArray}");
    public static RefClass NBTTagIntArray = getRefClass("{nms}.NBTTagIntArray, {nm}.nbt.NBTTagIntArray, {NBTTagIntArray}");
    public static RefClass NBTTagList = getRefClass("{nms}.NBTTagList, {nm}.nbt.NBTTagList, {NBTTagList}");
    public static RefClass NBTTagCompound = getRefClass("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");
    public static RefClass NBTException = getRefClass("{nms}.NBTException, {nm}.nbt.NBTException, {NBTException}");
    public static RefClass NBTCompressedStreamTools = getRefClass("{nms}.NBTCompressedStreamTools, {nm}.nbt.NBTCompressedStreamTools, {NBTCompressedStreamTools}, {nms}.CompressedStreamTools, {nm}.nbt.CompressedStreamTools, {CompressedStreamTools}");
    public static RefClass CraftEventFactory = getRefClass("{cb}.event.CraftEventFactory, {CraftEventFactory}");
    public static RefClass CraftMetaItem = getRefClass("{cb}.inventory.CraftMetaItem, {CraftMetaItem}");
    public static RefClass IInventory = getRefClass("{nms}.inventory.IInventory, {nm}.inventory.IInventory, {IInventory}");
    public static RefClass OreDictionary = getRefClass("net.minecraftforge.oredict.OreDictionary");
    public static RefMethod<Integer> IInventory_getSizeInventory = getMethod(IInventory, int.class, "getSizeInventory", null);

    public static RefField<String> CraftMetaItem_displayName = CraftMetaItem.getField("displayName");
    public static RefField<List<String>> CraftMetaItem_lore = CraftMetaItem.getField("lore");
    public static RefField<Map<Enchantment, Integer>> CraftMetaItem_enchantments = CraftMetaItem.getField("enchantments");
    public static RefClass Enchantment = getRefClass("{nms}.enchantment.Enchantment, {nm}.enchantment.Enchantment, {Enchantment}");
    public static RefField<Integer> Enchantment_effectId = getField(Enchantment, "effectId");
    public static RefField<Integer> CraftMetaItem_repairCost = CraftMetaItem.getField("repairCost");
    public static RefField CraftMetaItem_attributes = CraftMetaItem.getField("attributes");

    public static RefClass CraftItemStack = getRefClass("{cb}.inventory.CraftItemStack, {CraftItemStack}");
    public static RefField CraftItemStack_handle = CraftItemStack.getField("handle");
    public static RefClass ItemStack = getRefClass(CraftItemStack_handle);

    public static RefMethod IInventory_setInventorySlotContents = getMethod(IInventory, null, "setInventorySlotContents", new Object[]{int.class, ItemStack});

    public static RefField ItemStack_tag = getField(ItemStack, "stackTagCompound");
    public static RefField ItemStack_item = getField(ItemStack, "field_151002_e");
    public static RefClass Item = getRefClass(ItemStack_item);

    public static RefClass CraftInventory = getRefClass("{cb}.inventory.CraftInventory, {CraftInventory}");
    //Material
    public static RefClass Material = getRefClass(Material.class);
    public static RefField<HashMap<String, Material>> Material_BY_NAME = getField(Material, "BY_NAME");
    public static RefField<Material[]> Material_byId = getField(Material, "byId");

    //PlayerInventory
    public static RefClass CraftIBlock = getRefClass("{cb}.block.CraftBlock, {CraftBlock}");
    public static RefClass Block = getRefClass("{nms}.Block, {nm}.block.Block, {Block}");

    public static RefMethod CraftBlock_getNMSBlock = CraftIBlock.findMethod(new MethodCondition().withReturnType(Block).withTypes().withName("getNMSBlock"));
    public static RefClass CraftChunk = getRefClass("{cb}.CraftChunk, {CraftChunk}");

    public static RefClass Chunk = getRefClass("{nms}.Chunk, {nm}.world.chunk.Chunk, {Chunk}");
    public static RefMethod CraftChunk_getHandle = CraftChunk.findMethodByReturnType(Chunk);
    public static RefClass CraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");

    public static RefField CraftWorld_world = getField(CraftWorld, "world");

    public static RefClass World = getRefClass("{nms}.World, {nm}.world.World, {World}");
    public static RefClass CraftEntity = getRefClass("{cb}.entity.CraftEntity, {CraftEntity}");
    public static RefField CraftEntity_entity = getField(CraftEntity, "entity");
    public static RefClass Entity = getRefClass(CraftEntity_entity);

    public static RefClass CraftPlayer = getRefClass("{cb}.entity.CraftPlayer, {CraftPlayer}");
    public static RefClass EntityLivingBase = getRefClass("{nms}.EntityLivingBase, {nm}.entity.EntityLivingBase, {EntityLivingBase}");

    public static RefClass EntityPlayer = getRefClass("{nms}.EntityPlayer, {nm}.entity.player.EntityPlayer, {EntityPlayer}");
    public static RefField EntityPlayer_inventory = getField(EntityPlayer, "inventory");
    public static RefMethod CraftPlayer_getHandle = CraftPlayer.findMethodByReturnType(EntityPlayer);

    public static RefClass InventoryPlayer = getRefClass(EntityPlayer_inventory.getRealField().getType());
    public static RefField<Integer> InventoryPlayer_currentItem = getField(InventoryPlayer, "currentItem");

    public static RefClass EntityPlayerMP = getRefClass("{nms}.EntityPlayerMP, {nm}.entity.player.EntityPlayerMP, {EntityPlayerMP}");

    public static RefClass TileEntity = getRefClass("{nms}.TileEntity, {nm}.tileentity.TileEntity, {TileEntity}");
    public static RefMethod TileEntity_writeToNBT = getMethod(TileEntity, null, "writeToNBT", new Object[]{NBTTagCompound});

    public static RefMethod CraftWorld_getTileEntityAt = CraftWorld.findMethodByReturnType(TileEntity);// (int x, int y, int z)

    public static RefClass NetHandlerPlayServer = getRefClass("{nms}.NetHandlerPlayServer, {nm}.network.NetHandlerPlayServer, {NetHandlerPlayServer}, {nms}.PlayerConnection, {nm}.network.PlayerConnection, {PlayerConnection}");
    public static RefClass NetworkManager = getRefClass("{nms}.NetworkManager, {nm}.network.NetworkManager, {NetworkManager}");
    public static RefClass Packet = getRefClass("{nms}.Packet, {nm}.network.Packet, {Packet}");

    public static RefClass MovingObjectPosition = getRefClass("{nms}.MovingObjectPosition, {nm}.util.MovingObjectPosition, {MovingObjectPosition}");

    public static RefConstructor newEntityPlayerMP = getConstructor(EntityPlayerMP, new Object[]{MinecraftServer, WorldServer, getRefClass(GameProfile.class), ItemInWorldManager});
    public static RefConstructor newItemInWorldManager = getConstructor(ItemInWorldManager, new Object[]{World});

    public static RefField EntityPlayerMP_playerNetServerHandler = getField(EntityPlayerMP, "playerNetServerHandler");
    public static RefField NetHandlerPlayServer_netManager = getField(NetHandlerPlayServer, "netManager");
    public static RefField NetworkManager_channel = getField(NetworkManager, "channel");

    //worldServerForDimension
    public static RefMethod MinecraftServer_getWorldServer = getMethod(MinecraftServer, WorldServer, null, new Object[]{int.class});
    public static RefMethod CraftServer_getServer = getMethod(CraftServer, null, "getServer", null);
    public static RefMethod<Player> EntityPlayerMP_getBukkitEntity = getMethod(EntityPlayerMP, Player.class, "getBukkitEntity", null);

    public static RefMethod JsonToNBT_parse = getMethod(JsonToNBT, NBTBase, "func_150315_a", new Object[]{String.class});

    public static RefMethod NBTCompressedStreamTools_writeCompressed = getMethod(NBTCompressedStreamTools, null, "writeCompressed", new Object[]{NBTTagCompound, OutputStream.class});
    public static RefMethod NBTCompressedStreamTools_readCompressed = getMethod(NBTCompressedStreamTools, NBTTagCompound, "readCompressed", new Object[]{InputStream.class});

    public static RefMethod<ItemStack> CraftItemStack_asBukkitCopy = CraftItemStack.findMethodByName("asBukkitCopy");
    public static RefMethod<ItemStack> CraftItemStack_asCraftCopy = CraftItemStack.findMethodByName("asCraftCopy");
    public static RefMethod CraftItemStack_asNMSCopy = CraftItemStack.findMethodByName("asNMSCopy");
    public static RefMethod ItemStack_loadItemStackFromNBT = getMethod(ItemStack, ItemStack, "loadItemStackFromNBT", new Object[]{NBTTagCompound});
    public static RefMethod ItemStack_writeToNBT = getMethod(ItemStack, NBTTagCompound, "writeToNBT", new Object[]{NBTTagCompound});
    public static RefMethod<String> ItemStack_getDisplayName = getMethod(ItemStack, String.class, "getDisplayName", null);
    public static RefMethod ItemStack_useItemRightClick = getMethod(ItemStack, ItemStack, "useItemRightClick", new Object[]{World, EntityPlayer});
    public static RefMethod<Boolean> ItemStack_isItemEnchanted = getMethod(ItemStack, boolean.class, "isItemEnchanted", null);

    public static RefMethod Item_getMovingObjectPositionFromPlayer = getMethod(Item, MovingObjectPosition, "getMovingObjectPositionFromPlayer", new Object[]{World, EntityPlayer, boolean.class});
    public static RefMethod<String> Item_getItemStackDisplayName = getMethod(Item, String.class, "getItemStackDisplayName", new Object[]{ItemStack});
    public static RefMethod<Boolean> EntityPlayer_isUsingItem = getMethod(EntityPlayer, boolean.class, "isUsingItem", null);
    public static RefMethod EntityPlayerMP_closeContainer = getMethod(EntityPlayerMP, null, "closeContainer", null);
    public static RefMethod EntityPlayerMP_closeScreen = getMethod(EntityPlayerMP, null, "closeScreen", null);
    public static RefMethod EntityLivingBase_clearActivePotions = getMethod(EntityLivingBase, null, "clearActivePotions", null);
    public static RefMethod NetHandlerPlayServer_sendPacket = getMethod(NetHandlerPlayServer, null, "sendPacket", new Object[]{Packet});
//    public static RefField<Integer> Item_itemDamage = getField(Item, "itemDamage");
//    public static RefField Item_itemRegistry = getField(Item, "itemRegistry");
//    public static RefClass RegistryNamespaced = Item_itemRegistry.getRefClass();
//    public static RefMethod<String> RegistryNamespaced_getNameForObject = getMethod(RegistryNamespaced, String.class, "getNameForObject", new Object[]{Object.class});
    public static RefClass BlockLiquid = null;
    public static RefClass BlockFluidBase = null;

    public static void init() {
    }

    static {
        try {
            BlockLiquid = getRefClass("{nms}.BlockLiquid, {nm}.block.BlockLiquid, {BlockLiquid}");
            Clann.log("detect " + BlockLiquid.getRealClass().getName());
        } catch (Throwable e) {
            try {
                BlockLiquid = getRefClass("{nms}.BlockFluids, {nm}.block.BlockFluids, {BlockFluids}");
                Clann.log("detect " + BlockLiquid.getRealClass().getName());
            } catch (Throwable ee) {
                Clann.log("not detect BlockLiquid");
            }
        }
        try {
            BlockFluidBase = getRefClass("net.minecraftforge.fluids.BlockFluidBase");
            Clann.log("detect " + BlockFluidBase.getRealClass().getName());
        } catch (Throwable e) {
            Clann.log("not detect BlockFluidBase");
        }
    }

    /**
     * 清除IInventory
     *
     * @param iInventory
     */
    public static void clearIInventory(Object iInventory) {
        int size = IInventory_getSizeInventory.of(iInventory).call();
        for (int i = 0; i < size; i++) {
            IInventory_setInventorySlotContents.of(iInventory).call(i, null);
        }
    }

    public static RefField getField(Object clazz, String name) {
        RefClass refClass = ClassApi.warpRefClass(clazz);
        return refClass.getField(ObscureUtil.getFieldSeargeName(refClass, name));
    }

    public static RefConstructor getConstructor(Object clazz, Object[] parms) {
        RefClass refClass = ClassApi.warpRefClass(clazz);
        return refClass.getConstructor(ClassApi.warpClasses(parms));
    }

    public static RefMethod getMethod(Object clazz, Object _return, String name, Object[] parms) {
        RefClass refClass = ClassApi.warpRefClass(clazz);
        if (name == null) {
            return refClass.findMethod(new MethodCondition().withReturnType(_return == null ? void.class : ClassApi.warpClass(_return)).withTypes(parms == null ? new Class[0] : ClassApi.warpClasses(parms)));
        } else {
            return refClass.findMethodByName(ObscureUtil.getMethodSeargeName(refClass, _return == null ? void.class : ClassApi.warpClass(_return), name, parms == null ? new Class[0] : ClassApi.warpClasses(parms)));
        }
    }

    public static byte[] NBTCompressedStreamTools_save(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (o instanceof NBTTagCompound) {
            NBTCompressedStreamTools_writeCompressed.call(((NBTTagCompound) o).getHandle(), baos);
        } else if (NBTTagCompound.isInstance(o)) {
            NBTCompressedStreamTools_writeCompressed.call(o, baos);
        } else {
            throw new IllegalParmException("未知类型:" + o.getClass().toString());
        }
        try {
            baos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static NBTTagCompound NBTCompressedStreamTools_load(byte[] bs) {
        ByteArrayInputStream in = new ByteArrayInputStream(bs);
        NBTTagCompound tag = new NBTTagCompound(true, NBTCompressedStreamTools_readCompressed.call(in));
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

}
