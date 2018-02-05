/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase;

import java.io.File;
import lol.clann.pluginbase.holder.ThreadHolder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lol.clann.pluginbase.api.Configable;
import lol.clann.pluginbase.api.ILogger;
import lol.clann.pluginbase.api.JSONData;
import org.bukkit.scheduler.*;

/**
 * 模块,子类只提供一个无参构造函数,在里面初始化
 *
 * @author zyp
 * @param <T>
 */
public abstract class Module<P extends BasePlugin, C extends ModuleConfiguration, D extends JSONData> implements ILogger, Configable {

    @Override
    public File getFile() {
        return holder.getFile();
    }

    @Override
    public File getDataFolder() {
        return new File(holder.getDataFolder(), name);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    protected final P holder;
    private D data = null;
    /**
     * 配置文件
     */
    public C config = null;
    private final Class<C> configClass;
    private final Class<D> dataClass;
    /**
     * 模块名称
     */
    private final String name;
    private final Logger logger;//记录器
    private final ThreadHolder tHolder = new ThreadHolder();//线程管理器
    private final ArrayList<String> depend = new ArrayList();//依赖的模块
    private boolean enable = false;
    public boolean run = true;

    public Module(P holder, String name, Class<C> config, Class<D> data, String[] depend) {
        BaseAPI.notNull(name, "模块名字不能为空");
        BaseAPI.notNull(holder, "模块所属插件不能为空");
        this.name = name;
        this.holder = holder;
        logger = Logger.getLogger(name);
        this.configClass = config;
        this.dataClass = data;
        if (depend != null) {
            this.depend.addAll(Arrays.asList(depend));
        }
        this.depend.trimToSize();
    }

    /**
     * 返回前置模块
     *
     * @return
     */
    public ArrayList<String> getDepend() {
        return depend;
    }

    /**
     * 重载数据
     */
    public final void reloadData() {
        data.reloadData();
    }

    /**
     * 保存数据
     */
    public final void save() {
        data.save();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Module other = (Module) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    /**
     * 托管线程
     *
     * @param t
     */
    public final void addTask(Thread t) {
        tHolder.add(t);
    }

    /**
     * 托管task
     *
     * @param t
     * @param bt
     */
    public final void addTask(BukkitTask t) {
        tHolder.add(t);
    }

    public boolean isEnable() {
        return enable;
    }

    /**
     * 调用此方法加载模块
     */
    public void enable() {
        try {
            if (configClass != null) {
                config = configClass.newInstance();
                reloadConfig();
            }
            if (dataClass != null) {
                data = dataClass.newInstance();
                reloadData();
            }
            enable = true;
            enable0();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public abstract void enable0();

    /**
     * 卸载模块时调用此方法
     */
    public final void disable() {
        run = false;
        enable = false;
        disable0();
        //关闭线程
        tHolder.cancelAll();
    }

    protected abstract void disable0();

    /**
     * 重载模块
     */
    public final void reloadConfig() {
        config.reloadConfig();
    }

    public final String getName() {
        return name;
    }
}
