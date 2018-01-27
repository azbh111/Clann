/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann;

import lol.clann.object.command.CustomCommand;
import lol.clann.object.command.SubCommandAnnotation;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author zyp
 */
public class Command extends CustomCommand {

    public Command() {
        super(Clann.plugin, "base");
    }

    @SubCommandAnnotation(mustPlayer = false, args = "(player)", des = "查看玩家afk状态")
    public void afk(CommandSender sender, String[] args) {
        sender.sendMessage("AFK:" + Clann.plugin.afkdata.data.get(args[1]).isAFK());
        sender.sendMessage("状态:" + Clann.plugin.afkdata.data.get(args[1]).actions.toString());
    }
}
