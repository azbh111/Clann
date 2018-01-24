/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Administrator
 */
public class TextApi {
    //s = RegexMatches.replaceAll(s, "§.", "");

    public static String removeColor(String s) {
        return s.replaceAll("§.", "");
    }

    public static String colorTraslateToText(String s) {
        return s.replaceAll("§", "&");
    }

    public static String colorTraslateToDisplay(String s) {
        return s.replaceAll("&", "§");
    }

    public static String[] removeColor(String[] ss) {
        for (int i = 0; i < ss.length; i++) {
            ss[i] = ss[i].replaceAll("§.", "");
        }
        return ss;
    }

    public static String[] colorTraslateToText(String[] ss) {
        for (int i = 0; i < ss.length; i++) {
            ss[i] = ss[i].replaceAll("§", "&");
        }
        return ss;
    }

    public static String[] colorTraslateToDisplay(String[] ss) {
        for (int i = 0; i < ss.length; i++) {
            ss[i] = ss[i].replaceAll("&", "§");
        }
        return ss;
    }

    /**
     * DNA分析 拼字检查 语音辨识 抄袭侦测 字符串相似度计算 返回0-1
     *
     * @createTime 2012-1-12
     */
    public static double similar(String str1, String str2) {
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
            }
        }
        //计算相似度
        return 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
    }

    //得到最小值
    public static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }
}
