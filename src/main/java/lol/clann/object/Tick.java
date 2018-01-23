/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object;

import java.util.*;

/**
 *
 * @author Administrator
 */
public class Tick {

    public int number_of_ttck = 0;

    private final long tps_interval = 600000L;  //tick表只存储指定时间内的tick信息 10min
    private LinkedList<Long> tickTImes = new LinkedList();

    /**
     * 获取n秒内的平均tps
     *
     * @param n
     * @return 
     */
    public float getTps(int n) {
        Long t = System.currentTimeMillis() - 1000 * n;
        int count = 0;
        Long tickTime;
        synchronized (tickTImes) {
            Iterator<Long> it = tickTImes.iterator();
            while (it.hasNext()) {
                tickTime = it.next();
                if (t <= tickTime) {
                    count++;
                } else {
                    break;
                }
            }
        }
        return ((float) count) / n;
    }

    public void doTick() {
        number_of_ttck++;
        long tickTime = System.currentTimeMillis();
        synchronized (tickTImes) {
            tickTImes.addFirst(tickTime);
            if (tickTime - tps_interval > tickTImes.getLast()) {
                tickTImes.removeLast();
            }
        }
    }
}
