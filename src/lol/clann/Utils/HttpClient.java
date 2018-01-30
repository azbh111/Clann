/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author zyp
 */
public class HttpClient {

    String url;
    Map<String, String> property = new LinkedHashMap();

    public HttpClient(String url) {
        this.url = url;
    }

    public void setRequestProperty(String key, String value) {
        property.put(key, value);
    }

    /**
     * 发送GET方法
     *
     * @return
     *
     * @throws Exception
     */
    public String get() throws Exception {
        String result;
        BufferedReader in = null;
        try {
            String url = this.url;
            // 设置通用的请求属性
            if (!property.isEmpty()) {
                url = url + "?";
                for (Map.Entry<String, String> en : property.entrySet()) {
                    url = url + en.getKey() + "=" + en.getValue() + "&";
                }
            }
            if (url.endsWith("&")) {
                url = url.substring(0, url.length() - 1);
            }
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // 建立实际的连接
            connection.connect();
            result = readContent(connection);
        } catch (Exception e) {
            throw e;
        } // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                throw e2;
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *
     * @return 所代表远程资源的响应结果
     */
    public String post(String param) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            property.entrySet().stream().forEach((en) -> connection.setRequestProperty(en.getKey(), en.getValue()));
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            result = readContent(connection);
        } catch (Exception e) {
            throw e;
        } //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw ex;
            }
        }
        return result;
    }

    /**
     * 读取全部数据,并判断是否是GZIP,并返回流
     *
     * @param connection
     *
     * @return
     *
     * @throws IOException
     */
    private static String readContent(URLConnection connection) throws IOException {
        InputStream is = connection.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bs = new byte[8192];
        int n;
        while ((n = is.read(bs)) != -1) {
            baos.write(bs, 0, n);
        }
        is.close();
        byte[] data = baos.toByteArray();
        baos.close();
        int ss = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
        BufferedReader br;
        // 定义 BufferedReader输入流来读取URL的响应
        if (data.length >= 2 && ss == GZIPInputStream.GZIP_MAGIC) {
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(data)), Charset.forName("UTF-8")));
        } else {
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), Charset.forName("UTF-8")));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

}
