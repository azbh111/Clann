/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.listener;

import java.util.*;
import lol.clann.Clann;
import lol.clann.api.ItemApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * 管理功能性GUI，只起点击作用
 */
public class InventoryClickInterval implements Listener {

    public static Map<String, Long> inverval = new HashMap();

    public static Map<String, Long> clickTime = new HashMap();

    public InventoryClickInterval(){
        Bukkit.getPluginManager().registerEvents(this, Clann.plugin);
    }
    
    public static void register(String title, long ms) {
        inverval.put(title, ms);
    }

    public static void unregister(String title) {
        inverval.remove(title);
        clickTime.remove(title);
    }

    /**
     * 点击间隔，放行的点击会在HIGHEST优先级被拦截
     * @param event 
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getInventory().getTitle();
        if (title != null && !title.isEmpty()) {
            Long inter = inverval.get(title);
            if (inter != null) {
                Long lastClick = clickTime.get(title);
                if (lastClick != null) {
                    if (System.currentTimeMillis() - lastClick < inter) {
                        event.setCancelled(true);
                    }
                }
                clickTime.put(title, System.currentTimeMillis());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick2(InventoryClickEvent event) {
        if (inverval.containsKey(event.getInventory().getTitle())) {
            event.setCancelled(true);
        }
    }
}
