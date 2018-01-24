/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import org.bukkit.block.Block;

/**
 *
 * @author Administrator
 */
public class BlockApi {

    public static boolean isEmpty(Block b) {
        if (b == null || b.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isFliudBlock(Block b) {
        if (!isEmpty(b)) {
            Object nmsBlock = ReflectApi.CraftBlock_getNMSBlock.of(b).call();
            if (b.isLiquid() || (ReflectApi.BlockFluidBase != null && ReflectApi.BlockFluidBase.isInstance(nmsBlock)) || (ReflectApi.BlockLiquid != null && ReflectApi.BlockLiquid.isInstance(nmsBlock))) {
                return true;
            }
        }
        return false;
    }
}
