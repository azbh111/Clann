/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann;

import lol.clann.pluginbase.Module;
import lol.clann.pluginbase.ModuleConfiguration;
import lol.clann.pluginbase.api.JSONData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Administrator
 */
public class DataIO extends Module<Clann,ModuleConfiguration,JSONData> {

    public DataIO() {
        super(Clann.plugin, "DataIO", null, null, null);
        holder.data = this;
    }

    public boolean isAFK(Player player) {
        return isAFK(player.getName());
    }

    public boolean isAFK(String name) {
        return holder.afkdata.isAFK(name);
    }

    /**
     * 返回指定时间(600s内)内的tps
     *
     * @param second 秒数
     *
     * @return
     */
    public float getTps(int second) {
        return holder.serverTick.getTps(second);
    }

    /**
     * 返回开服以来执行的tick数
     *
     * @return
     */
    public int getTick() {
        return holder.serverTick.getTick();
    }

    public int getThreadCount() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }
        return threadGroup.activeCount();
    }

    /**
     * 返回异步BukkitTask数量
     *
     * @return
     */
    public int getAsynchBukkitTaskCount() {
        return Bukkit.getScheduler().getActiveWorkers().size();
    }

    /**
     * 返回同步BukkitTask数量
     */
    public int getPendingBukkitTaskCount() {
        return Bukkit.getScheduler().getPendingTasks().size();
    }

    /**
     * 返回服务器生物总数
     *
     * @return
     */
    public int getLivingEntityCount() {
        int count = 0;
        for (World w : Bukkit.getWorlds()) {
            count += w.getLivingEntities().size();
        }
        return count;
    }

    @Override
    public void enable0() {
      
    }

    @Override
    protected void disable0() {
    }

}
