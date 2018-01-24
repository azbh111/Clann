package lol.clann.afk;

import lol.clann.Clann;
import java.io.*;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Administrator
 */
public class afkPlayer {

    public Player p;
    private ByteArrayOutputStream actions = new ByteArrayOutputStream();
    byte lastAction = 0;
    public boolean AFK = false;

    public afkPlayer(Player p) {
        this.p = p;
    }

    public void logAction(byte b) {
        actions.write(b);
        lastAction = b;
        //一百个时自动计算AFK状态
        if(actions.size() > 100){
            Bukkit.getScheduler().runTaskAsynchronously(Clann.plugin, new Runnable() {
                @Override
                public void run() {
                    isAFK();
                }
            });
        }
    }

    /**
     * 取得上一次的操作 为空是返回0
     *
     * @return
     */
    public byte getLastAction() {
        return lastAction;
    }

    /**
     * 判断是否AFK,并清空操作序列
     *
     * @return
     */
    public boolean isAFK() {
        if (actions.size() < 5) {
            AFK = true;
            return true;
        } else {
            byte[] bs = null;
            synchronized (actions) {
                bs = actions.toByteArray();
                actions.reset();
            }
            AFK = _isAFK(bs);
            return AFK;
        }
    }

    private boolean _isAFK(byte[] bs) {
        int[] stepLength = getStepInfo(bs);
        for (int step = stepLength[0]; step <= stepLength[1]; step++) {
            if (isSimilar(bs, step)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSimilar(byte[] bs, int step) {
        int size = bs.length;
        int i = 0, j = 0;
        while (i < size) {
            for (j = 0; i < size && j < step; i++, j++) {
                if (bs[i] != bs[j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] getStepInfo(byte[] bs) {
        Map<Byte, Integer> count = new HashMap();
        for (byte b : bs) {
            int n = 0;
            if (count.containsKey(b)) {
                n = count.get(b);
            }
            count.put(b, n + 1);
        }
        int min = count.size();
        int max = bs.length / 2;
        return new int[]{min > 0 ? min : 1, max > 0 ? max : 1};
    }

}
