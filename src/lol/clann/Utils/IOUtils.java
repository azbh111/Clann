/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 *
 * @author zyp
 */
public class IOUtils {

    /**
     * 复制流中的数据
     * <p>
     * 数据复制完毕后,函数不会主动关闭输入输出流
     * </p>
     *
     * @param pIPStream
     * 输入流
     * @param pOPStream
     * 输出流
     *
     * @return 复制的字节数
     *
     * @throws IOException
     * 读入或写入数据时发生IO异常
     */
    public static void transform(InputStream pIPStream, OutputStream pOPStream) throws IOException {
        int n;
        byte[] tBuff = new byte[8192];
        while ((n = pIPStream.read(tBuff)) != -1) {
            pOPStream.write(tBuff, 0, n);
        }
        pIPStream.close();
        pOPStream.close();
    }

    
    
    /**
     * 将流中的内容全部读取出来,并使用指定编码转换为String
     *
     * @param pIPStream
     * 输入流
     * @param pEncoding
     * 转换编码
     *
     * @return 读取到的内容
     *
     * @throws IOException
     * 读取数据时发生错误
     * @throws UnsupportedEncodingException
     */
    public static String readContent(InputStream pIPStream, Charset pEncoding) throws IOException {
        return readContent(new InputStreamReader(pIPStream, pEncoding));
    }

    /**
     * 将流中的内容全部读取出来
     *
     * @param pIPSReader
     * 输入流
     *
     * @return 读取到的内容
     *
     * @throws IOException
     * 读取数据时发生错误
     */
    public static String readContent(InputStreamReader pIPSReader) throws IOException {
        int n = 0;
        char[] buff = new char[8192];
        StringBuilder sb = new StringBuilder();
        while ((n = pIPSReader.read(buff)) != -1) {
            sb.append(buff, 0, n);
        }
        pIPSReader.close();
        return sb.toString();
    }

    /**
     * 将流中的内容全部读取出来
     *
     * @param pIStream
     * 输入流
     *
     * @return 读取到的内容
     *
     * @throws IOException
     * 读取数据时发生错误
     */
    public static byte[] readData(InputStream pIStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transform(pIStream, baos);
        return baos.toByteArray();
    }
    public static void writeBytes(OutputStream out,byte[] data) throws IOException{
        out.write(data);
    }
}
