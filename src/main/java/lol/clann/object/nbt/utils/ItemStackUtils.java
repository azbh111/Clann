package lol.clann.object.nbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;
import static lol.clann.Utils.ReflectionUtils.getRefClass;
import lol.clann.api.ReflectApi;
import lol.clann.object.Refection.MethodCondition;
import lol.clann.object.Refection.RefClass;
import lol.clann.object.Refection.RefConstructor;
import lol.clann.object.Refection.RefField;
import lol.clann.object.Refection.RefMethod;


public final class ItemStackUtils {

    /**
     * static access to utils
     */
    public static final ItemStackUtils itemStackUtils = new ItemStackUtils();



    public RefClass classCraftItemStack = getRefClass("{cb}.inventory.CraftItemStack, {CraftItemStack}");
    public RefClass classItemStack = getRefClass("{nms}.ItemStack, {nm}.item.ItemStack, {ItemStack}");
    public RefField itemHandle = classCraftItemStack.findField(classItemStack);
    public RefField tag = classItemStack.findField("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");

    public RefMethod asNMSCopy;
    public RefMethod asCraftMirror;
    public RefConstructor conNmsItemStack;
    public RefConstructor conCraftItemStack;
    public RefClass classItemMeta;

    private ItemStackUtils(){
        try {
            asNMSCopy = classCraftItemStack.findMethod(new MethodCondition()
                            .withTypes(ItemStack.class)
                            .withReturnType(classItemStack)
            );
            asCraftMirror = classCraftItemStack.findMethod(new MethodCondition()
                            .withTypes(classItemStack)
                            .withReturnType(classCraftItemStack)
            );
        } catch (Exception e) {
            conNmsItemStack =ReflectApi.getConstructor(classItemStack, new Object[]{int.class, int.class, int.class});
            conCraftItemStack = classCraftItemStack.getConstructor(new Object[]{classItemStack});
        }
        try {
            classItemMeta = getRefClass("org.bukkit.inventory.meta.ItemMeta");
        } catch (Exception e) {
            classItemMeta = null;
        }

    }

    private Object getTag(Object nmsItemStack) {
        return tag.of(nmsItemStack).get();
    }

    @SuppressWarnings("unchecked")
    private void setTag(Object nmsItemStack, Object nbtTagCompound) {
        tag.of(nmsItemStack).set(nbtTagCompound);
    }

    @SuppressWarnings("deprecation")
    public Object createNmsItemStack(ItemStack itemStack){
        if (asNMSCopy != null) {
            return asNMSCopy.call(itemStack);
        } else {
            int type = itemStack.getTypeId();
            int amount = itemStack.getAmount();
            int data = itemStack.getData().getData();
            return conNmsItemStack.create(type, amount, data);
        }
    }

    public ItemStack createCraftItemStack(Object nmsItemStack){
        if (asCraftMirror != null) {
            return (ItemStack) asCraftMirror.call(nmsItemStack);
        } else {
            return (ItemStack) conCraftItemStack.create(nmsItemStack);
        }
    }

    private Object getHandle(ItemStack cbItemStack){
        return itemHandle.of(cbItemStack).get();
    }

    public ItemStack createCraftItemStack(ItemStack item){
        return createCraftItemStack(createNmsItemStack(item));
    }

    public void setTag(ItemStack itemStack, Object nbtTagCompound){
        if (classCraftItemStack.isInstance(itemStack)) setTagCB(itemStack, nbtTagCompound);
        else if (classItemMeta != null) setTagOrigin(itemStack, nbtTagCompound);
    }

    public Object getTag(ItemStack itemStack){
        if (classCraftItemStack.isInstance(itemStack)) return getTagCB(itemStack);
        else if (classItemMeta != null) return getTagOrigin(itemStack);
        else return null;
    }

    @SuppressWarnings("unchecked")
    private void setTagCB(ItemStack itemStack, Object nbtTagCompound){
        Object nmsItemStack = getHandle(itemStack);
        setTag(nmsItemStack,nbtTagCompound);
    }

    private Object getTagCB(ItemStack itemStack){
        Object nmsItemStack = getHandle(itemStack);
        return getTag(nmsItemStack);
    }

    private void setTagOrigin(ItemStack itemStack, Object nbtTagCompound){
        if (nbtTagCompound == null) {
            itemStack.setItemMeta(null);
            return;
        }
        ItemStack copyNMSItemStack = createCraftItemStack(itemStack);
        try {
            setTagCB(copyNMSItemStack, nbtTagCompound);
            ItemMeta meta = copyNMSItemStack.getItemMeta();
            itemStack.setItemMeta(meta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getTagOrigin(ItemStack itemStack){
        ItemStack copyNMSItemStack = createCraftItemStack(itemStack);
        try {
            ItemMeta meta = itemStack.getItemMeta();
            copyNMSItemStack.setItemMeta(meta);
            return getTagCB(copyNMSItemStack);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "copy item meta", e);
        }
        return null;
    }

}











