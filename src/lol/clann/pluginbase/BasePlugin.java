/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase;

import lol.clann.pluginbase.holder.BasePluginHolder;
import java.io.*;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lol.clann.Utils.PackageScanner;
import lol.clann.pluginbase.api.AutoRegister;
import lol.clann.pluginbase.api.ILogger;
import lol.clann.pluginbase.holder.ModuleHolder;
import lol.clann.pluginbase.holder.ThreadHolder;
import lol.clann.object.bean.Beans;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author zyp
 */
public abstract class BasePlugin extends JavaPlugin implements ILogger {

    /**
     * 记录着本插件所有的类
     */
    public Map<String, Class> pluginClasses;
    private ThreadHolder taskHolder = new ThreadHolder();//线程管理器
    private ModuleHolder moduleHolder = new ModuleHolder();//线程管理器

    public void add(BukkitTask m) {
        taskHolder.add(m);
    }

    public void add(Thread m) {
        taskHolder.add(m);
    }

    public void add(Module m) {
        moduleHolder.add(m);
    }

    @Override
    public void onDisable() {
        moduleHolder.disableAll();//先卸载模块
        taskHolder.cancelAll();//关闭线程
        onDisable0();
    }

    @Override
    public void onLoad(){
        BasePluginHolder.add(this);
        initPluginClasses();//记录本插件所有类类,且所有类的静态块会被jvm调用
    }
    
    @Override
    public void onEnable() {
        saveDefaultConfig();//保存配置文件
        reloadConfig();
        initBeans();//注册Bean
        registerBeans();//自动注册所有被AutoRegister注解的类
        onEnable0();//调用子类方法
        registerModules();// 自动实例化所有模块
        moduleHolder.enableAll();//启用所有模块
    }

    /**
     * 子类应实现重载数据
     */
    protected abstract void reloadConfig0();

    /**
     * 重载模块
     */
    @Override
    public final void reloadConfig() {
        super.reloadConfig();
        reloadConfig0();
    }

    /**
     * 注册Bean
     */
    private void initBeans() {
        for (Class clazz : pluginClasses.values()) {
            if (Beans.isBeans(clazz)) {
                Beans.registerClass(clazz);
            }
        }
    }

    /**
     * 记录本插件所有类类,且所有类的静态块会被jvm调用
     */
    private void initPluginClasses() {
        List<String> list = PackageScanner.Scann(this);
        Map<String, Class> map = new HashMap();
        list.forEach((s) -> {
            try {
                Class clazz = Class.forName(s);
                map.put(clazz.getName(), clazz);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        pluginClasses = Collections.unmodifiableMap(map);
    }

    /**
     * 实例化所有模块
     */
    private void registerModules() {
        BaseAPI.loopCollection(pluginClasses.values(), c -> {
            if (Module.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                try {
                    Module m = (Module) c.newInstance();
                    moduleHolder.add(m);
                } catch (Exception e) {
                    e.printStackTrace();
                    logError("注册Module失败:" + c.getName());
                }
            }
        });
    }

    /**
     * 对含有AutoRegister注解的类进行实例化
     *
     * @param name
     */
    private void registerBeans() {
        BaseAPI.loopCollection(pluginClasses.values(), c -> {
            if (c.isAnnotationPresent(AutoRegister.class)) {
                try {
                    c.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    logError("注册Bean失败:" + c.getName());
                }
            }
        });
    }

    /**
     * 子类实现,插件加载时自动调用
     */
    public abstract void onDisable0();

    /**
     * 子类实现,插件卸载时自动调用
     */
    public abstract void onEnable0();

    /**
     * 返回插件的jar文件
     *
     * @return
     */
    @Override
    public File getFile() {
        return super.getFile();
    }

    /**
     * 返回插件的类加载器
     *
     * @return
     */
    public ClassLoader getClassLoader_() {
        return super.getClassLoader();
    }
}
