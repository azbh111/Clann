/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Administrator
 */
public class InventoryApi {

    /**
     * 返回第一个可叠堆该物品的格子
     *
     * @param inv
     * @param is
     */
    public static int firstPartial(Inventory inv, ItemStack is) {
        if (inv != null && is != null) {
            ItemStack filteredItem = ReflectApi.CraftItemStack_asCraftCopy.call(is);
            if (filteredItem != null) {
                ItemStack[] inventory = inv.getContents();
                for (int i = 0; i < inventory.length; i++) {
                    ItemStack cItem = inventory[i];
                    if ((cItem != null) && (cItem.getAmount() < cItem.getMaxStackSize()) && (cItem.isSimilar(filteredItem))) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
}
