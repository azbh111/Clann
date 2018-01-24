/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import lol.clann.Clann;
import static lol.clann.Clann.plugin;
import lol.clann.object.Tick;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Administrator
 */
//@AutoRegister.Register(plugin = "", type = "function")
public class ServerTick {

    private Tick tick;
    BukkitTask bt = null;

    public ServerTick() {
        tick = new Tick();
        bt = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                nextTick();
            }
        }, 20, 1);
        Clann.plugin.tasks.add(bt);
        Clann.serverTick = this;
    }

    /**
     * 计算指定时间内的平均TPS
     *
     * @param second
     * @return
     */
    public float getTps(int second) {
        return tick.getTps(second);
    }

    public int getTick() {
        return tick.number_of_ttck;
    }

    private void nextTick() {
        tick.doTick();
    }

    /**
     * 等待下一tick
     *
     */
    public void waitNextTick() {
        int n_tick = tick.number_of_ttck;
        while (n_tick == tick.number_of_ttck) {
            wait(tick);
        }
    }

    /**
     *
     * 判断是否已经doTick,如没有就等待下一tick，并在下一tick返回已执行tick数
     *
     */
    public int waitNextTick(int n_tick) {
        while (n_tick == tick.number_of_ttck) {
            wait(tick);
        }
        return tick.number_of_ttck;
    }

    /**
     * 等待n个tick 并返回现在的tick数
     *
     * @param tick
     * @return
     * @throws InterruptedException
     */
    public int waitTicks(int n) {
        int ticks = tick.number_of_ttck + n;
        while (ticks > tick.number_of_ttck) {
            wait(tick);
        }
        return tick.number_of_ttck;
    }

    public void cancel() {
        bt.cancel();
    }

    private void wait(Object o) {
        try {
            synchronized (o) {
                o.wait();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 等待大于time毫秒的最小tick数
     *
     * @param time
     */
    public void dely(long time) {
        long end = System.currentTimeMillis() + time;
        while (end > System.currentTimeMillis()) {
            wait(tick);
        }
    }
}
