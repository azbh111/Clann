/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import static lol.clann.api.ReflectApi.CraftPlayer_getHandle;
import static lol.clann.api.ReflectApi.EntityPlayerMP_playerNetServerHandler;
import static lol.clann.api.ReflectApi.NetHandlerPlayServer_sendPacket;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author Administrator
 */
public class PlayerApi {

    static Object server = ReflectApi.CraftServer_getServer.of(Bukkit.getServer()).call();
    static Object world = ReflectApi.MinecraftServer_getWorldServer.of(server).call(0);
    static Object ItemInWorldManager = ReflectApi.newItemInWorldManager.create(world);

    /**
     * 计算玩家背包空格子数量
     *
     * @param p
     *
     * @return
     */
    public static int getInventoryEmpySlotCount(Player p) {
        PlayerInventory inv = p.getInventory();
        int n = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            if (ItemApi.isEmpty(inv.getItem(i))) {
                n++;
            }
        }
        return n;
    }

    public static void updateHeldItemSlot(Player p) {
        p.getInventory().setHeldItemSlot(p.getInventory().getHeldItemSlot());
    }

    public static void setHeldItemSlot(Player p, int i) {
        p.getInventory().setHeldItemSlot(i);
    }

    public static void sendPacket(Player p, Object packet) {
        NetHandlerPlayServer_sendPacket.of(EntityPlayerMP_playerNetServerHandler.of(CraftPlayer_getHandle.of(p).call()).get()).call(packet);
    }

    public static String getServerIp(Player p) {
        Object EntityPlayerMp = ReflectApi.CraftPlayer_getHandle.of(p).call();
        Object playerNetServerHandler = ReflectApi.EntityPlayerMP_playerNetServerHandler.of(EntityPlayerMp).get();
        Object netManager = ReflectApi.NetHandlerPlayServer_netManager.of(playerNetServerHandler).get();
        Channel c = (Channel) ReflectApi.NetworkManager_channel.of(netManager).get();
        return c.localAddress().toString().replaceFirst("/", "").split(":", 2)[0];
    }

    public static String getPlayerIp(Player p) {
        return p.getAddress().getHostString().replaceFirst("/", "");
    }

    public static boolean isUsingItem(Player p) {
        return ReflectApi.EntityPlayer_isUsingItem.of(ReflectApi.CraftPlayer_getHandle.of(p).call()).call();
    }

    //将OfflinePlayer转换为Player
    public static Player getOnlinePlayer(OfflinePlayer p) {
        if (p == null) {
            return null;
        }
        Player target = Bukkit.getPlayer(p.getName());
        if (target != null) {
            return target;
        }
        GameProfile profile = new GameProfile(p.getUniqueId(), p.getName());
        Object entity = ReflectApi.newEntityPlayerMP.create(server, world, profile, ItemInWorldManager);
        target = (entity == null) ? null : ReflectApi.EntityPlayerMP_getBukkitEntity.of(entity).call();
        if (target == null) {
            return null;
        }
        target.loadData();
        return target;
    }

    public static Player getOnlinePlayer(String name) {
        if (name != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            if (op != null) {
                return getOnlinePlayer(op);
            }
        }
        return null;
    }

    public static Boolean isOp(CommandSender sender) {
        if (sender.isOp() && sender instanceof Player) {
            return true;
        }
        return false;
    }

    public static Boolean isCommonPlayer(CommandSender sender) {
        if (sender instanceof Player && !sender.isOp()) {
            return true;
        }
        return false;
    }

    public static Boolean isConsole(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }
        return false;
    }

    public static Boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        }
        return false;
    }

}
