package lol.clann.Utils;

import java.util.Iterator;
import java.util.List;

public class ArrayUtils {

    /**
     * 转换为int数组
     * @param li
     * @return 
     */
    public static int[] toIntArray(List<Integer> li){
        int[] arr = new int[li.size()];
        int i = 0;
        Iterator<Integer> it = li.iterator();
        while(it.hasNext()){
            arr[i] = it.next();
            i++;    
        }
        return arr;
    }
    
    /**
     * 生成int数组
     *
     * @param start 起始
     * @param end 结束(不含)
     *
     * @return
     */
    public static int[] range(int start, int end) {
        return range(start, end, 1);
    }

    /**
     * 从0开始生成int数组
     *
     * @param end 结束,不含
     *
     * @return
     */
    public static int[] range(int end) {
        return range(0, end);
    }

    /**
     * 生成int数组
     *
     * @param start 起始
     * @param end 结束(不含)
     * @param step 步长
     *
     * @return
     */
    public static int[] range(int start, int end, int step) {
        int d = end - start;
        int[] arr;
        if (d % step == 0) {
            arr = new int[(end - start) / step];
        } else {
            arr = new int[(end - start) / step + 1];
        }
        for (int index = 0; index < arr.length; index++) {
            arr[index] = index * step + start;
        }
        return arr;
    }

    /**
     * 生成从0开始步长为1的int迭代器
     *
     * @param end 结束,不含
     *
     * @return
     */
    public static IntGenerator range_iterator(int end) {
        return range_iterator(0, end);
    }

    /**
     * 生成步长为1的int迭代器
     *
     * @param start 起始
     * @param end 结束,不含
     *
     * @return
     */
    public static IntGenerator range_iterator(int start, int end) {
        return range_iterator(start, end, 1);
    }

    /**
     * 生成int迭代器
     *
     * @param start 起始
     * @param end 结束,不含
     * @param step 步长
     *
     * @return
     */
    public static IntGenerator range_iterator(int start, int end, int step) {
        return new IntGenerator(start, end, step);
    }

    public static class IntGenerator implements Iterator {

        int start;
        int end;
        int step;
        int next;

        public IntGenerator(int start, int end, int step) {
            this.start = start;
            this.end = end;
            this.step = step;
            next = start;
        }

        @Override
        public boolean hasNext() {
            return next < end;
        }
        int re;

        @Override
        public Integer next() {
            re = next;
            next += step;
            return re;
        }

        public int[] toArray() {
            return range(start, end, step);
        }
    }

}
