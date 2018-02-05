/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import static lol.clann.api.DataApi.hexDigits;
import static lol.clann.api.DataApi.transfer;

/**
 * 提供对二进制数据的处理
 *
 * @author zyp
 */
public class DataUtils {

    public static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 二进制数据转换为Hex字符串
     *
     * @param pByteData
     *
     * @return
     */
    public static String byteToHex(byte[] bytes) {
        StringBuilder tSB = new StringBuilder();
        for (byte sB : bytes) {
            tSB.append(hexChars[(sB >> 4) & 0x0F]);
            tSB.append(hexChars[sB & 0x0F]);
        }
        return tSB.toString();
    }

    /**
     * Hex字符串还原为二进制数据
     *
     * @param hex
     * Hex字符串
     *
     * @return 还原后的字节数据
     *
     * @throws IllegalArgumentException
     * 输入的Hex字符串数据长度不是偶数
     */
    public static byte[] hexToByte(String hex) {
        if (hex == null || hex.length() == 0) {
            return new byte[0];
        }
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("长度必须为2的倍数");
        }
        byte[] bs = new byte[hex.length() / 2];
        char[] chars = hex.toCharArray();
        int[] sbs = new int[2];
        for (int i = 0, c = 0; i < chars.length; i += 2, c++) {
            for (int j = 0; j < 2; j++) {
                if (chars[i + j] >= '0' && chars[i + j] <= '9') {
                    sbs[j] = (chars[i + j] - '0');
                } else if (chars[i + j] >= 'A' && chars[i + j] <= 'F') {
                    sbs[j] = (chars[i + j] - 'A' + 10);
                } else if (chars[i + j] >= 'a' && chars[i + j] <= 'f') {
                    sbs[j] = (chars[i + j] - 'a' + 10);
                }
            }
            sbs[0] = (sbs[0] & 0x0f) << 4;
            sbs[1] = (sbs[1] & 0x0f);
            bs[c] = (byte) (sbs[0] | sbs[1]);
        }
        return bs;
    }

    /**
     * AES加密
     *
     * @param password 加密密码
     *
     * @return
     */
    public static byte[] AESEncrypt(byte[] bs, byte[] password) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password);
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化  
        return cipher.doFinal(bs);// 加密  
    }

    /**
     * AES解密
     *
     * @param content 待解密内容
     * @param password 解密密钥
     *
     * @return
     */
    public static byte[] AESDecrypt(byte[] content, byte[] password) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password);
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化  
        return cipher.doFinal(content);
    }

    /**
     * 反序列化
     *
     * @param bytes
     *
     * @return
     */
    public static Object deserializeObject(byte[] bytes) {
        try {
            ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object o = oi.readObject();
            oi.close();
            return o;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 序列化
     *
     * @param bytes
     *
     * @return
     */
    public static byte[] serializeObject(Object o) {
        try {
            ByteArrayOutputStream bi = new ByteArrayOutputStream();
            ObjectOutputStream oi = new ObjectOutputStream(bi);
            oi.writeObject(o);
            oi.close();
            bi.close();
            return bi.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * 计算byte数组的MD5
     *
     * @param bs
     * @return
     * @throws Exception
     */
    public static String MD5(byte[] bs){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bs);
            byte[] bytes = md.digest();
            return byteToHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static byte[] ungzip(byte[] o)   {
        GZIPInputStream g_in = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(o);
            g_in = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transfer(g_in, baos, 1024);
            g_in.close();
            bais.close();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                g_in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static byte[] gzip(byte[] o, int offset, int length) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(o, offset, length);
            gzip.close();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] gzip(byte[] o) throws IOException {
        return gzip(o, 0, o.length);
    }

    /**
     * 计算文件MD5
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String MD5(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileChannel ch = fis.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(byteBuffer);
            byte[] bytes = messagedigest.digest();
            return byteToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
