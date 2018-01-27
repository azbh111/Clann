package lol.clann.afk;

import java.util.*;
import org.bukkit.entity.Player;

/**
 *
 * @author Administrator
 */
public class AFKPlayer {

    public Player p;
    public final LinkedList<Byte> actions = new LinkedList();
    private final LinkedList<Long> times = new LinkedList();
    byte lastAction = 0;
    public boolean AFK = false;

    public AFKPlayer(Player p) {
        this.p = p;
    }

    public void refresh() {
        synchronized (actions) {
            if (actions.size() != times.size()) {
                new RuntimeException("事件表和事件时间表长度不等,actions.size()=" + actions.size() + ",times.size()=" + times.size() + ",重置数据").printStackTrace();
                actions.clear();
                times.clear();
                return;
            }
            Iterator<Byte> actIt = actions.iterator();
            Iterator<Long> timeIt = times.iterator();
            long now = System.currentTimeMillis();
            while (actIt.hasNext()) {
                actIt.next();
                long time = timeIt.next();
                if (now - time > 60000L) {
                    actIt.remove();
                    timeIt.remove();
                }
            }
        }
    }

    public void logAction(byte b) {
        actions.add(b);
        times.add(System.currentTimeMillis());
        if (actions.size() > 200) {//保持容量不超过200
            actions.removeFirst();
            times.removeFirst();
        }
        lastAction = b;
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
     * 计算AFK状态
     *
     * @return
     */
    public boolean isAFK() {
        if (actions.size() < 10) {
            AFK = true;//操作过少,判定为afk
        } else {
            byte[] bs = null;
            synchronized (actions) {//链表数据转移到数组
                bs = new byte[actions.size()];
                Iterator<Byte> it = actions.iterator();
                int p = 0;
                while (it.hasNext()) {
                    bs[p++] = it.next();
                }
            }
            AFK = _isAFK(bs);
        }
        return AFK;
    }

    /**
     * 计算
     *
     * @param bs
     *
     * @return
     */
    private boolean _isAFK(byte[] bs) {
        /**
         * i:主串指针
         * j:字串指针
         * l:子串长度
         */
        int i = 0, j, l;
        int maxLength = bs.length / 2;
        for (l = indexOf(bs, 1, bs[0]); 0 < l && l <= maxLength; l = indexOf(bs, l + 1, bs[0])) {
            for (i = l, j = 0;;) {
                if (bs[j] != bs[i]) {
                    break;//失配,增加子串长度
                } else {
                    //匹配,移动指针
                    i++;
                    j++;
                    if (i >= bs.length) {
                        //成功匹配到结尾,afk = true;
                        return true;
                    }
                    if (j >= l) {
                        //重复子串
                        j = 0;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 从指定位置开始查找目标,返回下标,找不到就返回-1
     *
     * @param bs
     * @param start
     * @param target
     *
     * @return
     */
    private int indexOf(byte[] bs, int start, byte target) {
        for (; start < bs.length; start++) {
            if (bs[start] == target) {
                return start;
            }
        }
        return -1;//没有找到
    }

}
