/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.afk;


import lol.clann.Clann;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class afkData {

    public static Map<Integer, Byte> actions = new HashMap<>();
    private Map<String, afkPlayer> afkData = new HashMap<>();
    Clann plugin;

    static {
        initIndex("PlayerMoveEvent", 1);
        initIndex("PlayerInteractEvent", 2);
        initIndex("BlockBreakEvent", 3);
        initIndex("BlockPlaceEvent", 4);
        initIndex("PlayerChangedWorldEvent", 5);
        initIndex("PlayerChatEvent", 6);
        initIndex("PlayerCommandPreprocessEvent", 7);
        initIndex("PlayerToggleSneakEvent", 8);
        initIndex("InventoryClickEvent", 9);
        initIndex("InventoryOpenEvent", 10);
        initIndex("PlayerToggleSprintEvent", 11);
        initIndex("PlayerDeathEvent", 12);
    }

    private static void initIndex(String name, int index) {
        actions.put(name.hashCode(), (byte) index);
    }

    public afkData(Clann plugin) {
        //初始化事件
        this.plugin = plugin;
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayer(p);
        }
        BukkitTask bt = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Collection<afkPlayer> c = afkData.values();
                for (afkPlayer ap : c) {
                    ap.isAFK();
                }
            }
        }, 20, 60 * 20);
        plugin.tasks.add(bt);
    }

    public void logActionUnsafe(String name, String event) {
        byte action = actions.get(event.hashCode());
        if (action != getLastAction(name)) {
            afkData.get(name).logAction(action);
        }
    }

    public void logActionSafe(String name, String event) {
        if (afkData.containsKey(name)) {
            logActionUnsafe(name, event);
        }
    }

    public byte getLastAction(String name) {
        return afkData.get(name).getLastAction();
    }

    public void addPlayer(Player p) {
        synchronized (afkData) {
            afkData.put(p.getName(), new afkPlayer(p));
        }
    }

    public void removePlayer(Player p) {
        synchronized (afkData) {
            afkData.remove(p);
        }
    }
    
    public boolean isAFK(String name){
        afkPlayer ap = afkData.get(name);
        if(ap != null){
            return ap.AFK;
        }
        return true;
    }
}
