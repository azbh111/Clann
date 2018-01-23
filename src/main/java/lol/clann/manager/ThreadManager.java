/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.manager;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lol.clann.Clann;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.*;

/**
 *
 * @author zyp
 */
public class ThreadManager implements Listener {

    private final static Map<String, List<BukkitTask>> taskMap = new HashMap();

    public ThreadManager() {
        Clann.log("插件线程管理器已启动");
    }

    public static void addTask(Plugin plugin, BukkitTask task) {
        synchronized (taskMap) {
            List<BukkitTask> a = taskMap.get(plugin.getName());
            if (a == null) {
                a = new LinkedList();
                taskMap.put(plugin.getName(), a);
            }
            a.add(task);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PluginDisableEvent e) {
        cancelAllTask(e.getPlugin());
    }

    private static void cancelAllTask(Plugin plugin) {
        synchronized (taskMap) {
            List<BukkitTask> a = taskMap.remove(plugin.getName());
            if (a != null && !a.isEmpty()) {
                a.stream().forEach((b) -> {
                    b.cancel();
                    Clann.log("终止线程(Owner:" + b.getOwner().getName() + ",TaskId:" + b.getTaskId() + ")");
                });
            }
        }
    }

    public static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
