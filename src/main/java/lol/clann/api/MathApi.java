/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class MathApi {

    public static NumberFormat num = NumberFormat.getPercentInstance();

    public static String getPercent(double d, int n) {
        num.setMaximumFractionDigits(n);
        return num.format(d);
    }

    public static String getPercent(float f, int n) {
        num.setMaximumFractionDigits(n);
        return num.format(f);
    }
    
    //n个元素进行全排列
    public static Integer permutate(int n) throws IOException {
        int[] is = new int[n];
        for (int i = 0; i < is.length; i++) {
            is[i] = i;
        }
        return permutate(is, 0, n - 1, 0);
    }

    //数组全排列
    private static Integer permutate(int[] is, int start, int end, int n) throws IOException {
        int j;
        int[] iss;
        if (start == end) {
            n++;
            /*
            此处为排列执行的代码
             */
            return n;
        }
        for (int i = start; i <= end; i++) {
            iss = is.clone();
            j = iss[start];
            iss[start] = iss[i];
            iss[i] = j;
            n = n + permutate(iss, start + 1, end, 0);
        }
        return n;
    }

    public static List<Integer> sortUp(List<Integer> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (list.get(i) > list.get(j)) {
                    int k = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, k);
                }
            }
        }
        return list;
    }
}
