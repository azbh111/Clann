/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.listener;

import lol.clann.Clann;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

/**
 *
 * @author Administrator
 */
public class afkListener implements Listener{
    
    Clann plugin;

    public afkListener(Clann aThis) {
        plugin = aThis;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerMoveEvent event) {
        //1
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "PlayerMoveEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerInteractEvent event) {
        //2
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "PlayerInteractEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(BlockBreakEvent event) {
        //3
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "BlockBreakEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(BlockPlaceEvent event) {  //固定上线地点会导致此事件在登陆事件前触发
        //4
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "BlockPlaceEvent");

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerChangedWorldEvent event) {
        //5
        plugin.afkdata.logActionSafe(event.getPlayer().getName(), "PlayerChangedWorldEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerChatEvent event) {
        //6
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "PlayerChatEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerCommandPreprocessEvent event) {
        //7
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "PlayerCommandPreprocessEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerToggleSneakEvent event) {
        //8
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "PlayerToggleSneakEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(InventoryClickEvent event) {
        //9
        HumanEntity he = event.getWhoClicked();
        if (he instanceof Player) {
            plugin.afkdata.logActionUnsafe(((Player) he).getName(), "InventoryClickEvent");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(InventoryOpenEvent event) {
        //10
        HumanEntity he = event.getPlayer();
        if (he instanceof Player) {
            plugin.afkdata.logActionUnsafe(((Player) he).getName(), "InventoryOpenEvent");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerToggleSprintEvent event) {
        //11
        plugin.afkdata.logActionUnsafe(event.getPlayer().getName(), "PlayerToggleSprintEvent");

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerDeathEvent event) {
        //12
        plugin.afkdata.logActionUnsafe(event.getEntity().getName(), "PlayerDeathEvent");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerJoinEvent event) {
        plugin.afkdata.addPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerQuitEvent event) {
        plugin.afkdata.removePlayer(event.getPlayer());
    }
}
