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
import lol.clann.Utils.BeanUtils;
import lol.clann.Utils.PackageScanner;
import lol.clann.pluginbase.api.AutoRegister;
import lol.clann.pluginbase.api.Configable;
import lol.clann.pluginbase.api.ILogger;
import lol.clann.pluginbase.holder.ModuleHolder;
import lol.clann.pluginbase.holder.ThreadHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author zyp
 */
public abstract class BasePlugin extends JavaPlugin implements ILogger ,Configable{

    /**
     * 记录着本插件所有的类
     */
    public Map<String, Class> pluginClasses;
    private ThreadHolder taskHolder = new ThreadHolder();//线程管理器
    private ModuleHolder moduleHolder;//模块管理器

    public final Module getModule(String name){
        return moduleHolder.get(name);
    }
    
    public final void add(BukkitTask m) {
        taskHolder.add(m);
    }

    public final void add(Thread m) {
        taskHolder.add(m);
    }

    public final void add(Module m) {
        moduleHolder.add(m);
    }

    @Override
    public final void onDisable() {
        onDisable0();
        moduleHolder.disableAll();//先卸载模块
        taskHolder.cancelAll();//关闭线程
    }

    @Override
    public final void onLoad() {
        BasePluginHolder.add(this);
        initPluginClasses();//记录本插件所有类类,且所有类的静态块会被jvm调用
        moduleHolder = new ModuleHolder(this);//模块管理器
    }

    @Override
    public final void onEnable() {
        saveDefaultConfig();//保存配置文件
        reloadConfig();
        initBeans();//注册Bean
        registerModules();// 自动实例化所有模块
        onEnable0();//调用子类方法
        moduleHolder.enableAll();//启用所有模块
        autoRegister();//自动注册,这些没有依赖关系,所以最后注册
    }

    /**
     * 子类应实现重载数据
     */
    protected abstract void reloadConfig0();

    private void autoRegister() {
        BaseAPI.loopCollection(pluginClasses.values(), c -> {
            if(c.isAnnotationPresent(AutoRegister.class)){
                try {
                    c.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

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
            if (BeanUtils.isBeans(clazz)) {
                BeanUtils.registerClass(clazz);
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
                logError("类初始化失败:"+s);
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
    public final File getFile() {
        return super.getFile();
    }

    /**
     * 返回插件的类加载器
     *
     * @return
     */
    public final ClassLoader getClassLoader_() {
        return super.getClassLoader();
    }
}
