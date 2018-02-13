/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.lang.CharUtils;

/**
 *
 * @author Administrator
 */
public class StringUtil extends org.apache.commons.lang.StringUtils {

    /**
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
     */
    static final char DBC_CHAR_START = 33; // 半角!    

    /**
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
     */
    static final char DBC_CHAR_END = 126; // 半角~    

    /**
     * 全角对应于ASCII表的可见字符从！开始，偏移值为65281
     */
    static final char SBC_CHAR_START = 65281; // 全角！    

    /**
     * 全角对应于ASCII表的可见字符到～结束，偏移值为65374
     */
    static final char SBC_CHAR_END = 65374; // 全角～    

    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
     */
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔    

    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
     */
    static final char SBC_SPACE = 12288; // 全角空格 12288    

    /**
     * 半角空格的值，在ASCII中为32(Decimal)
     */
    static final char DBC_SPACE = ' '; // 半角空格   

    /**
     * 检测二进制数据是GBK还是UTF-8
     */
    public static final String detectCharset(byte[] byteArray) {
        // 建立InputStream
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);

        // 默认编码
        String utf8 = "UTF-8";
        String charset = "GBK";

        // 开始检测是否为UTF-8
        try {
            // 标记初始位置
            bais.mark(0);

            // 读取前3字节
            byte[] first3Bytes = new byte[3];
            bais.read(first3Bytes);

            // 如果前三字节为 0xEFBBBF ，则为带签名的UTF-8
            if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                return utf8;
            }

            // 前三字节判定失败，开始检测是否是不带签名的UTF-8
            // 重置读取位置
            bais.reset();

            // 逐字节判定，直到遇到一个UTF-8编码字符
            byte[] oneByte = new byte[1];
            boolean isUtf8 = false;
            while (-1 != bais.read(oneByte)) {
                // 如果是ASCII码，跳过
                if (CharUtils.isAscii((char) oneByte[0])) {
                    continue;
                }

                // 双字节格式
                // 110yyyyy(C0-DF) 10xxxxxx
                if ((oneByte[0] & 0xE0) == 0xC0) {
                    bais.mark(0);
                    byte[] nextOneByte = new byte[1];
                    if (bais.available() >= 1 && -1 != bais.read(nextOneByte)) {
                        if ((nextOneByte[0] & 0xC0) == 0x80) {

                            // 是GBK双字节重叠部分？暂时当GBK处理，中文系统下，GBK默认编码，UTF-8不常见
                            // 双字节，第一个字节的值从0x81到0xFE，第二个字节的值从0x40到0xFE（不包括0x7F）
                            int oneByteInt = oneByte[0] & 0xff;
                            int nextOneByteInt = nextOneByte[0] & 0xff;
                            if (((0x81 & 0xff) <= oneByteInt && oneByteInt <= (0xFE & 0xff))
                                    && ((0x40 & 0xff) <= nextOneByteInt && nextOneByteInt <= (0xfe & 0xff))
                                    && (nextOneByte[0] != 0x7F)) {
                                continue;
                            }

                            // 非GBK重叠部分，归于UTF-8
                            isUtf8 = true;
                            break;
                        }
                        bais.reset();
                    }
                }

                // 三字节格式
                // 1110xxxx(E0-EF) 10xxxxxx 10xxxxxx
                if ((oneByte[0] & 0xF0) == 0xE0) {
                    byte[] twoByte = new byte[2];
                    bais.mark(0);
                    if (bais.available() >= 2 && -1 != bais.read(twoByte)) {
                        if (((twoByte[0] & 0xC0) == 0x80) && ((twoByte[1] & 0xC0) == 0x80)) {
                            isUtf8 = true;
                            break;
                        }
                        bais.reset();
                    }
                }

                // 四字节格式
                // 11110www(F0-F7) 10xxxxxx 10xxxxxx 10xxxxxx
                if ((oneByte[0] & 0xF8) == 0xF0) {
                    byte[] threeByte = new byte[3];
                    bais.mark(0);
                    if (bais.available() >= 3 && -1 != bais.read(threeByte)) {
                        if (((threeByte[0] & 0xC0) == 0x80) && ((threeByte[1] & 0xC0) == 0x80)
                                && ((threeByte[2] & 0xC0) == 0x80)) {
                            isUtf8 = true;
                            break;
                        }
                        bais.reset();
                    }
                }

                // 五字节格式
                // 111110xx(F8-FB) 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
                if ((oneByte[0] & 0xFC) == 0xF8) {
                    byte[] fourByte = new byte[4];
                    bais.mark(0);
                    if (bais.available() >= 4 && -1 != bais.read(fourByte)) {
                        if (((fourByte[0] & 0xC0) == 0x80) && ((fourByte[1] & 0xC0) == 0x80)
                                && ((fourByte[2] & 0xC0) == 0x80)
                                && ((fourByte[3] & 0xC0) == 0x80)) {
                            isUtf8 = true;
                            break;
                        }
                        bais.reset();
                    }
                }

                // 六字节格式
                // 1111110x(FC-FD) 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
                if ((oneByte[0] & 0xFE) == 0xFC) {
                    byte[] fiveByte = new byte[5];
                    bais.mark(0);
                    if (bais.available() >= 5 && -1 != bais.read(fiveByte)) {
                        if (((fiveByte[0] & 0xC0) == 0x80) && ((fiveByte[1] & 0xC0) == 0x80)
                                && ((fiveByte[2] & 0xC0) == 0x80)
                                && ((fiveByte[3] & 0xC0) == 0x80)
                                && ((fiveByte[4] & 0xC0) == 0x80)) {
                            isUtf8 = true;
                            break;
                        }
                        bais.reset();
                    }
                }
            }

            // 依据标志位设定返回值
            if (isUtf8) {
                return utf8;
            }
        } catch (IOException e) {
        }

        // 返回字符编码格式
        return charset;
    }

    /**
     *
     * 半角字符->全角字符转换
     * 只处理空格，!到˜之间的字符，忽略其他
     *
     */
    private static String half2full(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代    
                buf.append(SBC_SPACE);
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符    
                buf.append((char) (ca[i] + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理    
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    /**
     *
     * 全角字符->半角字符转换
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他
     *
     */
    public static String full2half(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < src.length(); i++) {
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内    
                buf.append((char) (ca[i] - CONVERT_STEP));
            } else if (ca[i] == SBC_SPACE) { // 如果是全角空格    
                buf.append(DBC_SPACE);
            } else { // 不处理全角空格，全角！到全角～区间外的字符    
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

}
