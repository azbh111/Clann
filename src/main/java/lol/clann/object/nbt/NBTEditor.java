/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.nbt;

import java.util.ArrayList;
import java.util.List;
import lol.clann.api.NBTApi;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Administrator
 */
public class NBTEditor<T> {

    private final T handle;
    private final NBTTagCompound tag;
    private final List<Object> roots = new ArrayList();
    private final List<NBTBase> os = new ArrayList();
    private NBTBase now;

    public NBTEditor(T handle) {
        this.handle = handle;
        if (handle instanceof ItemStack) {
            tag = NBTApi.getNBTTagCompound((ItemStack) handle);
        } else if (handle instanceof Block) {
            tag = NBTApi.getNBTTagCompound((Block) handle);
        } else if (handle instanceof Entity) {
            tag = NBTApi.getNBTTagCompound((Entity) handle);
        } else {
            throw new UnsupportedOperationException("未知参数:" + handle.toString());
        }
        os.add(now = tag);
    }

    public NBTEditor(NBTTagCompound tag) {
        this.tag = tag;
        os.add(tag);
        now = tag;
        handle = null;
    }

    public NBTBase getNow() {
        return now;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public T getHandle() {
        return handle;
    }

    public boolean canSave() {
        return handle != null;
    }

    /**
     * 只有当handle != null时才允许调用
     */
    public void save() {
        Validate.isTrue(canSave(), "当前没有处于直接编辑物品/实体/方块的模式，无法直接保存");
        if (handle instanceof ItemStack) {
            NBTApi.setNBTTagCompound((ItemStack) handle, tag);
        } else if (handle instanceof Block) {
            NBTApi.setNBTTagCompound((Block) handle, tag);
        } else if (handle instanceof Entity) {
            NBTApi.setNBTTagCompound((Entity) handle, tag);
        } else {
            throw new UnsupportedOperationException("未知参数:" + handle.toString());
        }
    }

    public NBTEditor reset() {
        this.roots.clear();
        this.os.clear();
        now = tag;
        os.add(tag);
        return this;
    }

    public NBTEditor remove(String[] roots) throws IllegalArgumentException {
        reset();
        for (int i = 0; i < roots.length - 1; i++) {
            next(roots[i]);
        }
        remove(roots[roots.length - 1]);
        return this;
    }

    public NBTEditor set(String[] roots, String value) {
        reset();
        for (int i = 0; i < roots.length - 1; i++) {
            next(roots[i]);
        }
        set(roots[roots.length - 1], value);
        return this;
    }

    public boolean contains(String[] roots, String value) {
        try {
            reset();
            for (int i = 0; i < roots.length - 1; i++) {
                next(roots[i]);
            }
            NBTBase o = null;
            if (now instanceof NBTTagList) {
                o = ((NBTTagList) now).get(Integer.valueOf(roots[roots.length - 1]));
            } else if (now instanceof NBTTagCompound) {
                o = ((NBTTagCompound) now).get(roots[roots.length - 1]);
            }
            if (o == null) {
                return false;
            }
            if (value.equals("*") || o.toJson(false).equals(value)) {
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
        } catch (Throwable ee) {
            ee.printStackTrace();
        }
        return false;
    }

    public NBTEditor remove(String key) throws IllegalArgumentException {
        Validate.isTrue(now != null && now instanceof NBTTagCompound, "要移除当前分支的子分支，请使用指令/nbt remove key");
        NBTTagCompound tag = (NBTTagCompound) now;
        tag.remove(key);
        return this;
    }

    public NBTEditor remove(int index) throws IllegalArgumentException {
        Validate.isTrue(now != null && now instanceof NBTTagList, "要移除当前分支的子分支，请使用指令/nbt remove index");
        NBTTagList tag = (NBTTagList) now;
        tag.remove(index);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object o : roots) {
            sb.append("§c").append(o).append("§f|");
        }
        sb.append("\n§f");
        sb.append(now.toJson(true));
        return sb.toString();
    }

    private void next(Object key, NBTBase o) {
        roots.add(key);
        os.add(o);
        now = o;
    }

    public NBTEditor next(String key) {
        if (now == null) {
            NBTBase o = tag.get(key);
            Validate.notNull(o, "不存在此键:" + key);
            Validate.isTrue(o instanceof NBTTagList || o instanceof NBTTagCompound, "该key对应的是基础值，请直接修改/nbt  set key value或删除/nbt remove key");
            next(key, o);
        } else if (now instanceof NBTTagCompound) {
            NBTBase o = ((NBTTagCompound) now).get(key);
            Validate.notNull(o, "不存在此键:" + key);
            Validate.isTrue(o instanceof NBTTagList || o instanceof NBTTagCompound, "该key对应的是基础值，请直接修改/nbt  set key value或删除/nbt remove key");
            next(key, o);
        } else {
            int index;
            try {
                index = Integer.valueOf(key);
            } catch (Exception e) {
                throw new NumberFormatException("当前分支应使用指令/nbt get index");
            }
            Validate.isTrue(now instanceof NBTTagList, "非法操作");
            NBTBase o = ((NBTTagList) now).get(index);
            Validate.isTrue(o instanceof NBTTagList || o instanceof NBTTagCompound, "该index对应的是基础值，请直接修改/nbt  set index value或删除/nbt remove index");
            next(index, o);
        }
        return this;
    }

    public NBTEditor pre() {
        Validate.isTrue(roots.size() >= 1 && os.size() >= 2, "当前处于顶层分支，无法执行该操作");
        roots.remove(roots.size() - 1);
        os.remove(os.size() - 1);
        now = os.get(os.size() - 1);
        return this;
    }

    public NBTEditor set(String key, String value) {
        NBTBase o = wrap(value);
        if (now == null) {
            tag.put(key, o);
        } else if (now instanceof NBTTagCompound) {
            ((NBTTagCompound) now).put(key, o);
        } else {
            Validate.isTrue(now instanceof NBTTagList, "非法操作");
            int index = -1;
            try {
                index = Integer.valueOf(key);
            } catch (Exception e) {
                throw new UnknownError("index必须为非负整数:" + key);
            }
            add((NBTTagList) now, index, o);
        }
        return this;
    }

    public NBTEditor set(int index, String value) {
        NBTBase o = wrap(value);
        Validate.isTrue(now != null && now instanceof NBTTagList, "当前分支应使用指令/nbt set key value");
        add((NBTTagList) now, index, o);
        return this;
    }

    private void add(NBTTagList o, int index, NBTBase base) {
        if (index >= o.size()) {
            o.add(base);
        } else {
            o.set(index, base);
        }
    }

    private NBTBase wrap(String value) {
        switch (value) {
            case "{}":
                return new NBTTagCompound();
            case "[]":
                return new NBTTagList();
            default:
                NBTBase base = new JsonToNBT.Primitive(null, value).wrap();
                if (base instanceof NBTTagString) {
                    NBTTagString s = (NBTTagString) base;
                    s.set(s.get().replace("&", "§"));
                }
                return base;
        }
    }
}
