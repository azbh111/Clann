/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase.holder;

import java.util.LinkedList;
import lol.clann.pluginbase.BaseAPI;
import org.bukkit.scheduler.BukkitTask;

/**
 * 线程管理器
 *
 * @author zyp
 */
public class ThreadHolder {

    private final LinkedList<BukkitTask> tasks = new LinkedList();
    private final LinkedList<Thread> threads = new LinkedList();

    public void add(BukkitTask o) {
        tasks.add(o);
    }

    public void add(Thread o) {
        threads.add(o);
    }

    public void cancelAll() {
        BaseAPI.loopCollection(tasks, o -> o.cancel());
        BaseAPI.loopCollection(threads, o -> o.interrupt());
        tasks.clear();
        threads.clear();
    }
}
