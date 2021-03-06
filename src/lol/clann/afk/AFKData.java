/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.afk;

import java.lang.reflect.Method;
import lol.clann.Clann;
import java.util.HashMap;
import java.util.Map;
import lol.clann.ClannAPI;
import lol.clann.pluginbase.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class AFKData extends Module {

    /**
     * actions还可以继续优化,直接使用事件名称的hashCode,从而无需此数组
     */
    public static final Map<String, Byte> actions = new HashMap<>();
    public final Map<String, AFKPlayer> data = new HashMap<>();
    Clann plugin;

    static {
        byte index = 1;
        for (Method m : AFKListener.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(AFKAnnotation.class)) {
                AFKAnnotation anno = m.getAnnotation(AFKAnnotation.class);
                if (anno.index() != 0) {
                    actions.put(anno.event(), anno.index());
                } else {
                    actions.put(anno.event(), index++);
                }
            }
        }
    }
    //会自动实例化
    public AFKData() {
        super(Clann.plugin, "AFKManager", null, null, null);
        Clann.plugin.afkdata = this;
    }

    /**
     * 记录事件
     *
     * @param name 玩家
     * @param event 事件
     */
    public void logAction(String name, String event) {
        byte action = actions.get(event);
        AFKPlayer p = data.get(name);
        if (p != null) {
            p.logAction(action);
        } else {
            ClannAPI.log("玩家" + name + "未在线");
        }
    }

    /*
     *
     * 获取玩家上次动作
     */
    public byte getLastAction(String name) {
        return data.get(name).getLastAction();
    }

    public final void addPlayer(Player p) {
        synchronized (data) {
            data.put(p.getName(), new AFKPlayer(p));
        }
    }

    public void removePlayer(Player p) {
        synchronized (data) {
            data.remove(p);
        }
    }

    /**
     * 返回玩家afk状态
     *
     * @param name
     *
     * @return
     */
    public boolean isAFK(String name) {
        AFKPlayer ap = data.get(name);
        if (ap != null) {
            return ap.AFK;
        }
        return true;
    }

    @Override
    public void enable0() {
        //初始化事件
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayer(p);
        }
        BukkitTask bt = Bukkit.getScheduler().runTaskTimerAsynchronously(holder, new Runnable() {
            @Override
            public void run() {
                synchronized (data) {
                    for (AFKPlayer ap : data.values()) {
                        ap.isAFK();//更新afk状态
                        ap.refresh();//清除陈旧数据
                    }
                }
            }
        }, 20, 60 * 20);//一分钟检测一次
        addTask(bt);
        
    }

    @Override
    protected void disable0() {

    }
}
