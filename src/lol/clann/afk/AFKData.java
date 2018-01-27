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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class AFKData {

    /**
     * actions还可以继续优化,直接使用事件名称的hashCode,从而无需此数组
     */
    public static final Map<String, Byte> actions = new HashMap<>();
    public final Map<String, AFKPlayer> data = new HashMap<>();
    Clann plugin;

    static {
        byte index = 1;
        for (Method m : AFKData.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(AFKAnnotation.class)) {
                AFKAnnotation anno = m.getAnnotation(AFKAnnotation.class);
                if (anno.index() != 0) {
                    actions.put(m.getAnnotation(AFKAnnotation.class).event(), anno.index());
                } else {
                    actions.put(m.getAnnotation(AFKAnnotation.class).event(), index++);
                }
            }
        }
    }

    public AFKData(Clann plugin) {
        //初始化事件
        this.plugin = plugin;
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayer(p);
        }
        BukkitTask bt = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
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
        plugin.tasks.add(bt);
    }

    /**
     * 记录事件
     *
     * @param name 玩家
     * @param event 事件
     */
    public void logAction(String name, String event) {
        byte action = actions.get(event);
        data.get(name).logAction(action);
    }

    /*
     *
     * 获取玩家上次动作
     */
    public byte getLastAction(String name) {
        return data.get(name).getLastAction();
    }

    public void addPlayer(Player p) {
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
}
