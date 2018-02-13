/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann;

import java.io.IOException;
import java.util.ArrayList;
import lol.clann.api.ItemApi;
import lol.clann.api.PlayerApi;
import lol.clann.object.command.CEException;
import lol.clann.object.command.CustomCommand;
import lol.clann.object.command.SubCommandAnnotation;
import lol.clann.pluginbase.api.AutoRegister;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author zyp
 */
@AutoRegister
public class Command extends CustomCommand {
    
    public Command() {
        super(Clann.plugin, "base");
    }
    
    @SubCommandAnnotation(mustPlayer = false, args = "(player)", des = "查看玩家afk状态")
    public void afk(CommandSender sender, String[] args) {
        sender.sendMessage("AFK:" + Clann.plugin.afkdata.data.get(args[0]).isAFK());
        sender.sendMessage("状态:" + Clann.plugin.afkdata.data.get(args[0]).actions.toString());
    }
    
    @SubCommandAnnotation(mustPlayer = true, args = "(key)", des = "用key保存背包物品")
    public void saveInv(Player player, String[] args) {
        ArrayList<ItemStack> arr = new ArrayList();
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (!ItemApi.isEmpty(is)) {
                arr.add(is);
            }
        }
        if (arr.isEmpty()) {
            player.sendMessage("您背包为空");
            return;
        }
        try {
            ItemApi.saveItemStacks(args[0], arr.toArray(new ItemStack[arr.size()]));
        } catch (IOException ex) {
            throw new CEException("保存异常", ex);
        }
        player.sendMessage("保存成功,key=" + args[0]);
    }
    
    @SubCommandAnnotation(mustPlayer = true, args = "(key)", des = "读取key对应的物品")
    public void getInv(Player player, String[] args) {
        ItemStack[] iss;
        try {
            iss = ItemApi.getItemStacks(args[0]);
        } catch (Exception e) {
            throw new CEException("加载Inv失败,key=" + args[0], e);
        }
        int n = PlayerApi.getInventoryEmpySlotCount(player);
        if (n > iss.length) {
            player.sendMessage("您的背包空间不足");
            return;
        }
        player.getInventory().addItem(iss);
        player.sendMessage("读取成功");
    }
}
