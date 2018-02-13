/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.*;
import java.util.*;
import lol.clann.Clann;
import lol.clann.Utils.FileUtils;
import lol.clann.Utils.StringUtil;
import lol.clann.exception.*;
import lol.clann.object.command.CEException;
import lol.clann.object.nbt.*;
import lol.clann.tellraw.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class ItemApi {

    public static final Map EnchantmentMap = new HashMap();
    public static Map<String, String> nameMap = new HashMap();
    public static Map<String, String> Attributes = new HashMap();

    static {
        Attributes.put("最大生命值", "generic.maxHealth");
        Attributes.put("生物跟随距离", "generic.followRange");
        Attributes.put("击退抗性", "generic.knockbackResistance");
        Attributes.put("速度", "generic.movementSpeed");
        Attributes.put("攻击伤害", "generic.attackDamage");
        Attributes.put("generic.maxHealth", "最大生命值");
        Attributes.put("generic.followRange", "生物跟随距离");
        Attributes.put("generic.knockbackResistance", "击退抗性");
        Attributes.put("generic.movementSpeed", "速度");
        Attributes.put("generic.attackDamage", "攻击伤害");

    }

    public static ItemStack setTag(ItemStack is, String[] roots, String value) {
        NBTEditor editor = new NBTEditor(NBTApi.getNBTTagCompound(is));
        editor.set(roots, value);
        NBTContainerItem c = new NBTContainerItem(is);
        c.writeTag(editor.getTag());
        return c.getObject();
    }

    /**
     * 移除指定nbt数据
     *
     * @param item
     * @param tag
     *
     * @return
     */
    public static ItemStack removeTag(ItemStack is, String[] roots) {
        NBTContainerItem c = new NBTContainerItem(is);
        NBTTagCompound tag = c.readTag();
        if (tag == null) {
            return is;
        }
        c.writeTag(NBTApi.removeTag(tag, roots));
        return c.getObject();
    }

    public static Object asNMSCopy(ItemStack is) {
        if (ReflectApi.CraftItemStack.isInstance(is)) {
            return ReflectApi.CraftItemStack_handle.of(is).get();
        } else {
            return ReflectApi.CraftItemStack_asNMSCopy.call(is);
        }
    }

    /**
     * 源自BossShopRe
     *
     * @param o
     *
     * @return
     */
    private static String getNBTFormatJson(Object o) {
        if (o == null) {
            return "{}";
        }
        NBTBase pNBTBase;
        if (o instanceof NBTBase) {
            pNBTBase = (NBTBase) o;
        } else if (ReflectApi.NBTBase.isInstance(o)) {
            pNBTBase = NBTBase.wrap(o);
        } else {
            return "{}";
        }
        if (pNBTBase instanceof NBTTagCompound) {
            Map<String, Object> tNBTContents = ((NBTTagCompound) pNBTBase).getHandleMap();
            if (tNBTContents == null || tNBTContents.isEmpty()) {
                return "{}";
            }
            String contentJson = "{";
            for (Map.Entry<String, Object> en : tNBTContents.entrySet()) {
                if (en.getValue() != null) {
                    contentJson += en.getKey() + ':' + getNBTFormatJson(en.getValue()) + ',';
                }
            }
            if (contentJson.lastIndexOf(",") != -1) {
                contentJson = contentJson.substring(0, contentJson.length() - 1);
            }
            return contentJson + "}";
        } else if (pNBTBase instanceof NBTTagList) {
            List<Object> tNBTContents = ((NBTTagList) pNBTBase).getHandleList();
            if (tNBTContents == null || tNBTContents.isEmpty()) {
                return "[]";
            }
            String contentJson = "[";
            int i = 0;
            for (Object tContentNode : tNBTContents) {
                if (tContentNode == null) {
                    continue;
                }
                contentJson += i + ":" + getNBTFormatJson(tContentNode) + ',';
                i++;
            }
            if (contentJson.lastIndexOf(",") != -1) {
                contentJson = contentJson.substring(0, contentJson.length() - 1);
            }
            return contentJson + "]";
        } else if (pNBTBase instanceof NBTTagString) {
            String value = ((NBTTagString) pNBTBase).get();
            value = value.replace("\"", "\\\"");
            if (StringUtil.isNotBlank(value) && value.charAt(value.length() - 1) == '\\') {
                value = value + " ";
            }
            return "\"" + value + "\"";
        } else {
            return pNBTBase.getHandle().toString();
        }
    }

    /**
     * 源自BossShopRe
     *
     * @param pItem
     *
     * @return
     */
    private static String getItemJson(ItemStack pItem) {
        if (isEmpty(pItem)) {
            return "{}";
        }
        StringBuilder itemJson = new StringBuilder("{id:");
        Object tNMSItem = asNMSCopy(pItem);
        if (tNMSItem != null) {
            NBTTagCompound tag = new NBTTagCompound();
            ReflectApi.ItemStack_writeToNBT.of(tNMSItem).call(tag.getHandle());
            itemJson.append(tag.get("id").getHandle());
        } else {
            itemJson.append(pItem.getTypeId()).append('s');
        }
        itemJson.append(",Damage:").append(pItem.getDurability());
        NBTTagCompound tagNBTTagCompound = NBTApi.getNBTTagCompound(pItem);
        if (tagNBTTagCompound.getHandle() != null) {
            itemJson.append(",tag:").append(getNBTFormatJson(tagNBTTagCompound));
        }
        itemJson.append('}');
        return itemJson.toString();
    }

    public static Tellraw getMessage(ItemStack is, String command) {
        Tellraw itemMsg = new Tellraw(LanguageApi.getDisplayName(is));
        itemMsg.getChatStyle().setHoverEvent(HoverEvent.Action.show_item, getItemJson(is));
        itemMsg.getChatStyle().setClickEvent(ClickEvent.Action.run_command, command);
        return itemMsg;
    }

    public static Tellraw getMessage(ItemStack is) {
        Tellraw itemMsg = new Tellraw(LanguageApi.getDisplayName(is));
        itemMsg.getChatStyle().setHoverEvent(HoverEvent.Action.show_item, getItemJson(is));
        return itemMsg;
    }

    /**
     * ItemStack保存为NBTTagCompound
     *
     * @param is
     *
     * @return
     */
    public static NBTTagCompound ItemStack2NBTTagCompound(ItemStack is) {
        NBTTagCompound tag = new NBTTagCompound();
        ReflectApi.ItemStack_writeToNBT.of(ReflectApi.CraftItemStack_handle.of(is).get()).call(tag.getHandle());
        tag.put("id", is.getType().name());
        return tag;
    }

    /**
     * 由NBTTagCompound生成ItemStack
     *
     * @param tag
     *
     * @return
     */
    public static ItemStack NBTTagCompound2ItemStack(NBTTagCompound tag) {
        String type = tag.getString("id");
        Material ma = Material.getMaterial(type);
        if (ma == null) {
            throw new NullMaterialException("未知Material:" + type);
        }
        tag.put("id", (short) ma.getId());
        return ReflectApi.CraftItemStack_asBukkitCopy.call(ReflectApi.ItemStack_loadItemStackFromNBT.call(tag.getHandle()));
    }

    /**
     * 判断物品是否为空(null air)
     *
     * @param is
     *
     * @return
     */
    public static boolean isEmpty(ItemStack is) {
        if (is == null || is.getType().equals(Material.AIR)) {
            return true;
        }
        return false;
    }

    /**
     * 设置物品显示名字
     *
     * @param is
     * @param name
     *
     * @return
     */
    public static ItemStack setName(ItemStack is, String name) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName("§f" + name.replace('&', '§'));
        is.setItemMeta(meta);
        return is;
    }

    /**
     * 返回非空Lore
     *
     * @param is
     *
     * @return
     */
    public static List<String> getLore(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        if (meta == null) {
            return new ArrayList();
        }
        List<String> lore = ReflectApi.CraftMetaItem_lore.of(meta).get();
        if (lore == null) {
            lore = new ArrayList();
        }
        return lore;
    }

    /**
     * 设置Lore
     *
     * @param is
     * @param s
     *
     * @return
     */
    public static ItemStack setLore(ItemStack is, List<String> lore) {
        //new Exception().printStackTrace();
        NBTContainerItem c = new NBTContainerItem(is);
        NBTTagCompound tag = c.readTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        NBTTagCompound display = tag.getCompound("display");
        if (display == null) {
            display = new NBTTagCompound();
        }
        if (lore == null || lore.isEmpty()) {
            display.remove("Lore");
        } else {
            NBTTagList Lore = new NBTTagList();
            for (String s : lore) {
                Lore.add(new NBTTagString(s));
            }
            display.put("Lore", Lore);
        }

        tag.put("display", display);
        c.writeTag(tag);
        //String s = NBTApi.NBTTagCompound2String(tag, false);
        //System.out.println(s);
        return c.getObject();
    }

    /**
     * 设置Lore
     *
     * @param is
     * @param s
     *
     * @return
     */
    public static ItemStack setLore(ItemStack is, int index, String add) {
        List<String> lore = getLore(is);
        CollectionsApi.set(lore, index, "§f" + add.replace('&', '§'));
        setLore(is, lore);
        return is;
    }

    /**
     * 为物品添加Lore，支持颜色字符
     *
     * @param is
     * @param s
     *
     * @return
     */
    public static ItemStack addLore(ItemStack is, String s) {
        List<String> lore = getLore(is);
        lore.add("§f" + s.replace('&', '§'));
        return setLore(is, lore);
    }

    /**
     * 移除物品所有Lore
     *
     * @param item
     *
     * @return
     */
    public static ItemStack removeLore(ItemStack is) {
        return setLore(is, null);
    }

    /**
     * 移除物品指定Lore
     *
     * @param item
     * @param i
     *
     * @return
     */
    public static ItemStack removeLore(ItemStack is, int i) {
        List<String> lore = getLore(is);
        CollectionsApi.remove(lore, i);
        return setLore(is, lore);
    }

    /**
     *
     * 判断两个物品是否相似
     */
    public static boolean IsSimilar(ItemStack i1, ItemStack i2) {
        if ((i1.getTypeId() != i2.getTypeId()) || (i1.getDurability() != i2.getDurability())) {
            return false;
        }
        if (i1.isSimilar(i2)) {
            return true;
        }
        ItemStack item1 = i1.clone();
        ItemStack item2 = i2.clone();
        item1 = me.removeAddtionalData(item1);
        item2 = me.removeAddtionalData(item2);
        if (item1.isSimilar(item2)) {
            return true;
        }
        return false;
    }

    /**
     * 设置物品不掉耐久
     *
     * @param item
     *
     * @return
     */
    public static ItemStack setUnbreakable(ItemStack is, boolean Unbreakable) {
        NBTContainerItem c = new NBTContainerItem(is);
        NBTTagCompound tag = c.readTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        if (Unbreakable) {
            tag.put("Unbreakable", (byte) 1);
        } else {
            tag.remove("Unbreakable");
        }
        c.writeTag(tag);
        return c.getObject();
    }

    /**
     * 为装备添加属性 1.生命值generic.maxHealth 2.生物跟踪距离generic.followRange
     * 3.击退抗性generic.knockbackResistance 4.移动速度generic.movementSpeed
     * 5.攻击伤害generic.attackDamage
     *
     * @param item
     * @param type
     * @param amount
     *
     * @return
     */
    public static ItemStack addAttribute(ItemStack item, String type, double amount) {
        return me.addAttributeModifiers(item, Attributes.get(type), amount);
    }

    /**
     * 以键值key保存ItemStack
     *
     * @param plg
     * @param key
     * @param is
     */
    public static void saveItem(JavaPlugin plg, String key, ItemStack is) {
        FileOutputStream fos = null;
        try {
            File file = new File(plg.getDataFolder() + File.separator + "Item" + File.separator + key);
            if (!file.exists()) {
                File dir = file.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                file.createNewFile();
            }
            fos = new FileOutputStream(file, false);
            ItemStack2NBTTagCompound(is).writeGZip(fos);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                DataApi.close(fos);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 从键值key读取ItemStack
     *
     * @param plg
     * @param key
     *
     * @return
     */
    public static ItemStack getItem(JavaPlugin plg, String key) {
        FileInputStream fis = null;
        try {
            File file = new File(plg.getDataFolder() + File.separator + "Item" + File.separator + key);
            if (!file.exists()) {
                //System.out.println("物品不存在");
                return null;
            }
            fis = new FileInputStream(file);
            return NBTTagCompound2ItemStack(NBTTagCompound.readGZip(fis));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                DataApi.close(fis);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * 以键值key保存ItemStack[]
     * @param key
     * @param iss
     * @throws IOException 
     */
    public static void saveItemStacks(String key, ItemStack[] iss) throws IOException {
        saveItemStacks(Clann.plugin, key, iss);
    }

    /**
     * 以键值key保存ItemStack[]
     *
     * @param plg
     * @param key
     * @param iss 可以含null，会自动过滤
     *
     * @throws java.io.IOException
     */
    public static void saveItemStacks(JavaPlugin plg, String key, ItemStack[] iss) throws IOException {
        try {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            for (ItemStack is : iss) {
                if (!isEmpty(is)) {
                    items.add(ItemStack2NBTTagCompound(is));
                }
            }
            tag.put("items", items);
            File file = FileUtils.getFile(plg.getDataFolder().getPath() + File.separator + "Inventory" + File.separator + key, true);
            tag.writeGZip(new FileOutputStream(file, false));
        } catch (IOException ex) {
            throw ex;
        }
    }
    /**
     * 从键值key读取ItemStack[]
     * @param key
     * @param iss
     * @throws IOException 
     */
    public static ItemStack[] getItemStacks(String key) throws IOException {
        return getItemStacks(Clann.plugin, key);
    }

    /**
     * 从键值key读取ItemStack[]
     *
     * @param plg
     * @param key
     *
     * @return
     */
    public static ItemStack[] getItemStacks(JavaPlugin plg, String key) throws IOException {
        try {
            File file = new File(plg.getDataFolder() + File.separator + "Inventory" + File.separator + key);
            if (!file.exists()) {
                throw new CEException("文件不存在:" + file.getPath());
            }
            NBTTagList items = NBTTagCompound.readGZip(file).getList("items");
            ItemStack[] iss = new ItemStack[items.size()];
            for (int i = 0; i < items.size(); i++) {
                iss[i] = NBTTagCompound2ItemStack(items.getCompound(i));
            }
            return iss;
        } catch (IOException ex) {
            throw ex;
        }
    }

    public static ItemStack addEnchantment(ItemStack is, short id, short level) {
        return me.addEnchantment(is, getEnchantmentById(id), level);
    }

    public static ItemStack setEnchantment(ItemStack is, int index, short id, short level) {
        return me.setEnchantment(is, index, getEnchantmentById(id), level);
    }

    public static Enchantment getEnchantmentById(short id) {
        Enchantment en = Enchantment.getById(id);
        if (en == null) {
            throw new NullEnchantmentException("未知附魔ID:" + id);
        }
        return en;
    }

    public static ItemStack removeEnchantment(ItemStack is, int index) {
        return me.removeEnchantment(is, index);
    }

    public static ItemStack removeEnchantments(ItemStack is) {
        return me.removeEnchantments(is);
    }

    public static ItemStack removeAttributes(ItemStack is) {
        return me.removeAttributes(is);
    }

    public static ItemStack removeAttribute(ItemStack is, String name) {
        return me.removeAttribute(is, Attributes.get(name));
    }

    static class me {

        private static ItemStack removeAddtionalData(ItemStack is) {
            NBTContainerItem c = new NBTContainerItem(is);
            NBTTagCompound tag = c.readTag();
            if (tag == null) {
                return is;
            }
            c.writeTag(new NBTEditor(tag).remove(new String[]{"display"}).remove(new String[]{"ench"}).remove(new String[]{"RepairCost"}).getTag());
            return c.getObject();
        }

        /**
         * 为装备添加属性 1.生命值generic.maxHealth 2.跟随距离generic.followRange
         * 3.击退抗性generic.knockbackResistance 4.移动速度generic.movementSpeed
         * 5.攻击伤害generic.attackDamage
         *
         * @param item
         * @param type
         * @param amount
         *
         * @return
         */
        private static ItemStack addAttributeModifiers(ItemStack item, String type, double amount) {
            NBTContainerItem c = new NBTContainerItem(item);
            NBTTagCompound tag = c.readTag();
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            NBTTagList AttributeModifiers = tag.getList("AttributeModifiers");
            if (AttributeModifiers == null) {
                AttributeModifiers = new NBTTagList();
            }
            for (int i = 0; i < AttributeModifiers.size(); i++) {
                NBTBase base = AttributeModifiers.get(i);
                if (base instanceof NBTTagCompound) {
                    NBTTagCompound _tag = (NBTTagCompound) base;
                    if (type.equals(_tag.getString("AttributeName"))) {
                        _tag.put("Amount", amount);
                        AttributeModifiers.set(i, _tag);
                        tag.put("AttributeModifiers", AttributeModifiers);
                        c.writeTag(tag);
                        return c.getObject();
                    }
                }
            }
            AttributeModifiers.add(me.createAttributeModifiers(type, amount));
            tag.put("AttributeModifiers", AttributeModifiers);
            c.writeTag(tag);
            //System.out.println(NBTApi.NBTTagCompound2String(tag, false));
            return c.getObject();
        }

        private static NBTTagCompound createAttributeModifiers(String name, double num) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.put("UUIDMost", 2872L);
            tag.put("UUIDLeast", 894654L);
            tag.put("Amount", num);
            tag.put("AttributeName", name);
            tag.put("Operation", 0);
            tag.put("Name", name);
            return tag;
        }

        private static String getEnchKey(ItemStack is) {
            if (is.getType().equals(Material.ENCHANTED_BOOK)) {
                return "StoredEnchantments";
            } else {
                return "ench";
            }
        }

        /**
         * 为附魔书添加附魔
         *
         * @param itemstack
         * @param enchType
         * @param level
         *
         * @return
         *
         * @throws Exception
         */
        private static ItemStack addEnchantment(ItemStack is, Enchantment en, short level) {
            String key = getEnchKey(is);
            NBTContainerItem c = new NBTContainerItem(is);
            NBTTagCompound tag = c.readTag();
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            NBTTagList StoredEnchantments = tag.getList(key);
            if (StoredEnchantments == null) {
                StoredEnchantments = new NBTTagList();
            }
            NBTTagCompound ench = new NBTTagCompound();
            ench.put("id", (short) en.getId());
            ench.put("lvl", level);
            StoredEnchantments.add(ench);
            tag.put(key, StoredEnchantments);
            c.writeTag(tag);
            return c.getObject();
        }

        /**
         * 设置附魔书附魔
         *
         * @param itemstack
         * @param enchType
         * @param level
         *
         * @return
         *
         * @throws Exception
         */
        private static ItemStack setEnchantment(ItemStack is, int index, Enchantment en, short level) {
            String key = getEnchKey(is);
            NBTContainerItem c = new NBTContainerItem(is);
            NBTTagCompound tag = c.readTag();
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            NBTTagList enchantments = tag.getList(key);
            if (enchantments == null) {
                enchantments = new NBTTagList();
            }
            NBTTagCompound ench = new NBTTagCompound();
            ench.put("id", (short) en.getId());
            ench.put("lvl", level);
            if (index < 0) {
                enchantments.set(0, ench);
            } else if (index > enchantments.size()) {
                enchantments.set(enchantments.size() - 1, ench);
            } else {
                enchantments.set(index, ench);
            }
            tag.put(key, enchantments);
            c.writeTag(tag);
            return c.getObject();
        }

        /**
         * 设置附魔书附魔
         *
         * @param itemstack
         * @param enchType
         * @param level
         *
         * @return
         *
         * @throws Exception
         */
        private static ItemStack removeEnchantment(ItemStack is, int index) {
            String key = getEnchKey(is);
            NBTContainerItem c = new NBTContainerItem(is);
            NBTTagCompound tag = c.readTag();
            if (tag == null || !tag.containsKey(key)) {
                return is;
            }
            NBTTagList enchantments = tag.getList(key);
            if (enchantments == null) {
                return is;
            }
            CollectionsApi.remove(enchantments, index);
            tag.put(key, enchantments);
            c.writeTag(tag);
            return c.getObject();
        }

        /**
         * 移除物品所有附魔
         *
         * @param is
         *
         * @return
         */
        private static ItemStack removeEnchantments(ItemStack is) {
            String key = getEnchKey(is);
            NBTContainerItem c = new NBTContainerItem(is);
            NBTTagCompound tag = c.readTag();
            if (tag == null || !tag.containsKey(key)) {
                return is;
            } else {
                tag.remove(key);
                c.writeTag(tag);
                return c.getObject();
            }
        }

        /**
         * 移除物品所有属性
         *
         * @param is
         *
         * @return
         */
        private static ItemStack removeAttributes(ItemStack is) {
            NBTContainerItem c = new NBTContainerItem(is);
            NBTTagCompound tag = c.readTag();
            if (tag == null || !tag.containsKey("AttributeModifiers")) {
                return is;
            }
            tag.remove("AttributeModifiers");
            c.writeTag(tag);
            return c.getObject();
        }

        /**
         * 移除物品指定属性
         *
         * @param is
         *
         * @return
         */
        private static ItemStack removeAttribute(ItemStack is, String name) {
            NBTContainerItem c = new NBTContainerItem(is);
            NBTTagCompound tag = c.readTag();
            if (tag == null) {
                return is;
            }
            NBTTagList AttributeModifiers = tag.getList("AttributeModifiers");
            if (AttributeModifiers == null || AttributeModifiers.isEmpty()) {
                return is;
            }
            for (int i = 0; i < AttributeModifiers.size(); i++) {
                NBTBase base = AttributeModifiers.get(i);
                if (base instanceof NBTTagCompound) {
                    NBTTagCompound _tag = (NBTTagCompound) base;
                    if (name.equals(_tag.getString("AttributeName"))) {
                        AttributeModifiers.remove(i);
                        tag.put("AttributeModifiers", AttributeModifiers);
                        c.writeTag(tag);
                        return c.getObject();
                    }
                }
            }
            return is;
        }
    }
}
