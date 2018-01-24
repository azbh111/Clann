/*++
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Administrator
 */
public class DataApi {

    public static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static void main(String[] args) {

    }

    public static void close(Closeable... closeables) throws IOException {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
    }
    
    /**
     * @param password 加密密码
     * @return
     */
    public static byte[] encrypt(byte[] bs, byte[] password) throws Exception {
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
     * 解密
     *
     * @param content 待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content, byte[] password) throws Exception {
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
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    public static Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 序列化
     *
     * @param bytes
     * @return
     */
    public static byte[] ObjectToByte(Object o) {
        byte[] bs = null;
        try {
            ByteArrayOutputStream bi = new ByteArrayOutputStream();
            ObjectOutputStream oi = new ObjectOutputStream(bi);
            oi.writeObject(o);
            bs = bi.toByteArray();
            bi.close();
            oi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bs;
    }

    /**
     * byte数组转化为文件
     *
     * @param bsfile
     * @return
     * @throws IOException
     */
    public static File ByteToFile(byte[] bsfile, File file) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        ByteArrayInputStream bais = new ByteArrayInputStream(bsfile);
        byte[] bs = new byte[8192];
        int i = bais.read(bs);
        while (i != -1) {
            fos.write(bs);
            bais.read(bs);
        }
        fos.flush();
        fos.close();
        bais.close();
        return file;
    }

    /**
     * 计算byte数组的MD5
     *
     * @param bs
     * @return
     * @throws Exception
     */
    public static String getMD5ByByteArray(byte[] bs) throws Exception {
        String value;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bs);
        byte[] bytes = md.digest();
        StringBuffer stringbuffer = new StringBuffer(2 * bytes.length);
        int k = bytes.length;
        for (int l = 0; l < k; l++) {
            stringbuffer.append(hexDigits[(bytes[l] & 0xf0) >> 4]);
            stringbuffer.append(hexDigits[bytes[l] & 0xf]);
        }
        return stringbuffer.toString();
    }

    public static byte[] GUNZIP(byte[] o) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(o);
        GZIPInputStream g_in = new GZIPInputStream(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transfer(g_in, baos, 1024);
        g_in.close();
        bais.close();
        return baos.toByteArray();
    }

    public static byte[] GZIP(byte[] o, int offset, int length) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(o, offset, length);
        gzip.close();
        return baos.toByteArray();
    }

    public static byte[] GZIP(byte[] o) throws IOException {
        return GZIP(o, 0, o.length);
    }

    /**
     * 计算文件MD5
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String getMd5ByFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileChannel ch = fis.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(byteBuffer);
            byte[] bytes = messagedigest.digest();
            StringBuffer stringbuffer = new StringBuffer(2 * bytes.length);
            int k = bytes.length;
            for (int l = 0; l < k; l++) {
                stringbuffer.append(hexDigits[(bytes[l] & 0xf0) >> 4]);
                stringbuffer.append(hexDigits[bytes[l] & 0xf]);
            }
            fis.close();
            return stringbuffer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从输入流传输数据到输出流
     *
     * @param in 输入流
     * @param out 输出流
     * @param size 缓冲区大小
     */
    public static void transfer(InputStream in, OutputStream out, int size) throws IOException {
        byte[] bs = new byte[size];
        int i = 0;
        while ((i = in.read(bs)) != -1) {
            out.write(bs, 0, i);
        }
    }
}
