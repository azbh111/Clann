/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.util.concurrent.LinkedBlockingQueue;
import lol.clann.Clann;
import lol.clann.pluginbase.api.AutoRegister;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;


/**
 * 提供一个接口,让异步线程的指令在主线程里执行
 *
 * @author zyp
 */
@AutoRegister//自动初始化
public class CommandUtils {

    /**
     * 缓冲指令
     */
    private static LinkedBlockingQueue<command> buffer = new LinkedBlockingQueue();
    
    public CommandUtils() {
        BukkitTask bt = Bukkit.getScheduler().runTaskTimer(Clann.plugin, new Runnable() {
            @Override
            public void run() {
                while (!buffer.isEmpty()) {
                    command c = buffer.poll();
                    if (c == null) {
                        break;
                    }
                    Bukkit.dispatchCommand(c.sender, c.command);
                }
            }
        }, 1, 1);
        Clann.plugin.add(bt);
    }

    /**
     * 以控制台身份运行指令
     *
     * @param command
     */
    public static void runConsoleCommand(String command) {
        runCommand(Bukkit.getConsoleSender(), command);
    }

    /**
     * 以给定身份运行指令
     *
     * @param sender
     * @param command
     */
    public static void runCommand(CommandSender sender, String command) {
        buffer.add(new command(sender, command));
    }
    
    private static class command {
        
        CommandSender sender;
        String command;
        
        public command(CommandSender sender, String command) {
            this.command = command;
            this.sender = sender;
        }
    }
}
