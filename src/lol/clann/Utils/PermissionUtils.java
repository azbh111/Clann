/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author zyp
 */
public class PermissionUtils {

    /**
     * Vault提供的权限接口
     */
    public static Permission permission = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();

    /**
     * 判断玩家是否有指定权限
     * @param player
     * @param perm
     * @return 
     */
    public static boolean hasPermission(Player player, String perm) {
        return hasPermission(null, player.getName(), perm);
    }
    /**
     * 判断玩家是否有指定权限
     * @param player
     * @param perm
     * @return 
     */
    public static boolean hasPermission(String player, String perm) {
        return hasPermission(null, player, perm);
    }
    /**
     * 判断玩家是否有指定权限
     * @param world
     * @param player
     * @param perm
     * @return 
     */
    public static boolean hasPermission(String world, String player, String perm) {
        return permission.playerHas(world, player, perm);
    }
}
