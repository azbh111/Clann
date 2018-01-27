/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.afk;

import lol.clann.Clann;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;

/**
 *
 * @author Administrator
 */
public class AFKListener implements Listener {

    Clann plugin;

    public AFKListener(Clann aThis) {
        plugin = aThis;
    }

    @AFKAnnotation(event = "pMove", index = -99)
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerMoveEvent event) {
        //移动会产生大量事件,连续的移动只记录一次
        if (plugin.afkdata.getLastAction(event.getPlayer().getName()) != AFkData.actions.get("pMove")) {
            plugin.afkdata.logAction(event.getPlayer().getName(), "pMove");
        }
    }

    @AFKAnnotation(event = "pInteract")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerInteractEvent event) {
        plugin.afkdata.logAction(event.getPlayer().getName(), "pInteract");
    }

    @AFKAnnotation(event = "bBreak")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(BlockBreakEvent event) {
        plugin.afkdata.logAction(event.getPlayer().getName(), "bBreak");
    }

    @AFKAnnotation(event = "bPlace")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(BlockPlaceEvent event) {  //固定上线地点会导致此事件在登陆事件前触发
        plugin.afkdata.logAction(event.getPlayer().getName(), "bPlace");

    }

    @AFKAnnotation(event = "pChangeW")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerChangedWorldEvent event) {
        plugin.afkdata.logAction(event.getPlayer().getName(), "pChangeW");
    }

    @AFKAnnotation(event = "pChat")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerChatEvent event) {
        plugin.afkdata.logAction(event.getPlayer().getName(), "pChat");
    }

    @AFKAnnotation(event = "pCommand")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerCommandPreprocessEvent event) {
        plugin.afkdata.logAction(event.getPlayer().getName(), "pCommand");
    }

    @AFKAnnotation(event = "pSneak")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerToggleSneakEvent event) {
        plugin.afkdata.logAction(event.getPlayer().getName(), "pSneak");
    }

    @AFKAnnotation(event = "iClick")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(InventoryClickEvent event) {
        HumanEntity he = event.getWhoClicked();
        if (he instanceof Player) {
            plugin.afkdata.logAction(((Player) he).getName(), "iClick");
        }
    }

    @AFKAnnotation(event = "iOpen")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(InventoryOpenEvent event) {
        HumanEntity he = event.getPlayer();
        if (he instanceof Player) {
            plugin.afkdata.logAction(((Player) he).getName(), "iOpen");
        }
    }

    @AFKAnnotation(event = "pSprint")//冲刺
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerToggleSprintEvent event) {
        plugin.afkdata.logAction(event.getPlayer().getName(), "pSprint");

    }

    @AFKAnnotation(event = "pDeath")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerDeathEvent event) {
        plugin.afkdata.logAction(event.getEntity().getName(), "pDeath");
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
