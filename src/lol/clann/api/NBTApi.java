/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.util.*;
import lol.clann.object.nbt.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Administrator
 */
public class NBTApi {

    public static final String _NBTTagCompound = "C";
    public static final String _NBTTagList = "L";
    public static final String _NBTTagByte = "b";
    public static final String _NBTTagByteArray = "B";
    public static final String _NBTTagShort = "s";
    public static final String _NBTTagInt = "i";
    public static final String _NBTTagIntArray = "I";
    public static final String _NBTTagLong = "l";
    public static final String _NBTTagFloat = "f";
    public static final String _NBTTagDouble = "d";
    public static final String _NBTTagString = "S";

    public static final Map<String, Byte> Type2ID = new HashMap();
    public static final Map<Byte, String> ID2Type = new HashMap();

    static {
        Type2ID.put(_NBTTagCompound, NBTTagCompound.typeId);
        Type2ID.put(_NBTTagList, NBTTagList.typeId);
        Type2ID.put(_NBTTagByte, NBTTagByte.typeId);
        Type2ID.put(_NBTTagByteArray, NBTTagByteArray.typeId);
        Type2ID.put(_NBTTagShort, NBTTagShort.typeId);
        Type2ID.put(_NBTTagInt, NBTTagInt.typeId);
        Type2ID.put(_NBTTagIntArray, NBTTagIntArray.typeId);
        Type2ID.put(_NBTTagLong, NBTTagLong.typeId);
        Type2ID.put(_NBTTagFloat, NBTTagFloat.typeId);
        Type2ID.put(_NBTTagDouble, NBTTagDouble.typeId);
        Type2ID.put(_NBTTagString, NBTTagString.typeId);
        for (Map.Entry<String, Byte> en : Type2ID.entrySet()) {
            ID2Type.put(en.getValue(), en.getKey());
        }
    }

    public static String ID2Type(byte id) {
        switch (id) {
            case NBTTagCompound.typeId:
                return _NBTTagCompound;
            case NBTTagList.typeId:
                return _NBTTagList;
            case NBTTagByte.typeId:
                return _NBTTagByte;
            case NBTTagByteArray.typeId:
                return _NBTTagByteArray;
            case NBTTagShort.typeId:
                return _NBTTagShort;
            case NBTTagInt.typeId:
                return _NBTTagInt;
            case NBTTagIntArray.typeId:
                return _NBTTagIntArray;
            case NBTTagLong.typeId:
                return _NBTTagLong;
            case NBTTagFloat.typeId:
                return _NBTTagFloat;
            case NBTTagDouble.typeId:
                return _NBTTagDouble;
            case NBTTagString.typeId:
                return _NBTTagString;
            default:
                throw new IllegalArgumentException("未知类型ID:" + id);
        }
    }

    public static byte Type2ID(String type) {
        switch (type) {
            case _NBTTagCompound:
                return NBTTagCompound.typeId;
            case _NBTTagList:
                return NBTTagList.typeId;
            case _NBTTagByte:
                return NBTTagByte.typeId;
            case _NBTTagByteArray:
                return NBTTagByteArray.typeId;
            case _NBTTagShort:
                return NBTTagShort.typeId;
            case _NBTTagInt:
                return NBTTagInt.typeId;
            case _NBTTagIntArray:
                return NBTTagIntArray.typeId;
            case _NBTTagLong:
                return NBTTagLong.typeId;
            case _NBTTagFloat:
                return NBTTagFloat.typeId;
            case _NBTTagDouble:
                return NBTTagDouble.typeId;
            case _NBTTagString:
                return NBTTagString.typeId;
            default:
                throw new IllegalArgumentException("未知类型:" + type);
        }
    }

    /**
     * NBTTagCompound转为Map
     *
     * @param nbt
     * @return
     */
    public static Map<String, Object> NBTTagCompound2Map(NBTTagCompound nbt) {
        Map<String, Object> map = new HashMap();
        for (Map.Entry<String, NBTBase> en : nbt.entrySet()) {
            switch (en.getValue().getTypeId()) {
                case NBTTagCompound.typeId:
                    map.put(en.getKey(), NBTTagCompound2Map((NBTTagCompound) en.getValue()));
                    break;
                case NBTTagList.typeId:
                    map.put(en.getKey(), me.NBTTagList2List((NBTTagList) en.getValue()));
                    break;
                default:
                    map.put(en.getKey(), en.getValue().toJson(false));
            }
        }
        return map;
    }

    /**
     * Map转为NBTTagCompound
     *
     * @param map
     * @return
     */
    public static NBTTagCompound Map2NBTTagCompound(Map<String, Object> map) {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry<String, Object> en : map.entrySet()) {
            if (en.getValue() instanceof Map) {
                tag.put(en.getKey(), Map2NBTTagCompound((Map<String, Object>) en.getValue()));
            } else if (en.getValue() instanceof List) {
                tag.put(en.getKey(), me.List2NBTTagList((List) en.getValue()));
            } else {
                tag.put(en.getKey(), JsonToNBT.Primitive.wrap((String) en.getValue()));
            }
        }
        return tag;
    }

    /**
     * 移除指定nbt数据
     *
     * @param nbt
     * @param tag
     * @return
     */
    public static NBTTagCompound removeTag(NBTTagCompound nbt, String[] tag) {
        return new NBTEditor(nbt).remove(tag).getTag();
    }

    /**
     * 从物品取NBTTagCompound
     *
     * @param item
     * @return
     */
    public static NBTTagCompound getNBTTagCompound(ItemStack item) {
        Object o = ReflectApi.ItemStack_tag.of(ItemApi.asNMSCopy(item)).get();
        if (o != null) {
            return new NBTTagCompound(true, o);
        } else {
            return new NBTTagCompound();
        }
    }

    public static void setNBTTagCompound(ItemStack item, NBTTagCompound tag) {
        new NBTContainerItem(item).writeTag(tag);
    }

    /**
     * 从实体获取NBTTagCompound
     *
     * @param item
     * @return
     */
    public static NBTTagCompound getNBTTagCompound(Entity entity) {
        NBTTagCompound tag = new NBTContainerEntity(entity).readTag();
        return tag != null ? tag : new NBTTagCompound();
    }

    public static void setNBTTagCompound(Entity entity, NBTTagCompound tag) {
        new NBTContainerEntity(entity).writeTag(tag);
    }

    /**
     * 获取方块NBTCompound
     *
     * @param item
     * @return
     */
    public static NBTTagCompound getNBTTagCompound(Block block) {
        NBTTagCompound tag = new NBTContainerBlock(block).readTag();
        return tag != null ? tag : new NBTTagCompound();
    }

    public static void setNBTTagCompound(Block block, NBTTagCompound tag) {
        new NBTContainerBlock(block).writeTag(tag);
    }

    /**
     * nbt转为String，便于显示给玩家
     *
     * @param tag
     * @return
     */
    public static String NBTTagCompound2String(NBTTagCompound tag, boolean hasColor) {
        if (tag == null) {
            return "{}";
        }
        return tag.toJson(hasColor);
    }

    static class me {

        private static NBTTagCompound merge(NBTTagCompound tag, String[] args, int index) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            if (index == args.length - 2) {
                tag.put(args[index], NBTBase.fromJson(args[index + 1]));
            } else {
                NBTBase nb = tag.get(args[index]);
                if (nb instanceof NBTTagCompound) {
                    tag.put(args[index], merge((NBTTagCompound) nb, args, index + 1));
                } else if (nb instanceof NBTTagList) {
                    tag.put(args[index], merge((NBTTagList) nb, args, index + 1));
                } else {
                    tag.put(args[index], merge((NBTTagCompound) null, args, index + 1));
                }
            }
            return tag;
        }

        private static NBTTagList merge(NBTTagList list, String[] args, int index) {
            int pos = Integer.valueOf(args[index]);
            if (list == null) {
                list = new NBTTagList();
            }
            NBTBase nb = null;
            if (pos >= 0 && pos < list.size()) {
                nb = list.get(pos);
            }
            if (index == args.length - 2) {
                nb = NBTBase.fromJson(args[index + 1]);
            } else if (nb instanceof NBTTagCompound) {
                nb = merge((NBTTagCompound) nb, args, index + 1);
            } else if (nb instanceof NBTTagList) {
                nb = merge((NBTTagList) nb, args, index + 1);
            } else {
                nb = merge((NBTTagCompound) null, args, index + 1);;
            }
            if (pos >= list.size()) {
                list.add(nb);
            } else {
                list.set(pos, nb);
            }
            return list;
        }

        //done
        private static List NBTTagList2List(NBTTagList list) {
            int max = list.size();
            List out = new ArrayList();
            NBTBase o;
            for (int i = 0; i < max; i++) {
                o = list.get(i);
                if (o instanceof NBTTagCompound) {
                    out.add(NBTApi.NBTTagCompound2Map((NBTTagCompound) o));
                } else if (o instanceof NBTTagList) {
                    out.add(NBTTagList2List((NBTTagList) o));
                } else {
                    out.add(o.toJson(false));
                }
            }
            return out;
        }

        //done
        private static NBTTagList List2NBTTagList(List list) {
            NBTTagList tList = new NBTTagList();
            for (Object o : list) {
                if (o instanceof Map) {
                    tList.add(NBTApi.Map2NBTTagCompound((Map<String, Object>) o));
                } else if (o instanceof List) {
                    tList.add(List2NBTTagList((List) o));
                } else {
                    tList.add(NBTBase.fromJson((String) o));
                }
            }
            return tList;
        }

    }

}
