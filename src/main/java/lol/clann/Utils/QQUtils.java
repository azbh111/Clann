/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 *
 * @author Administrator
 */
public class QQUtils {

    public static DateFormat dateFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void main(String args[]) {
        String s = sendGet("http://qinfo.clt.qq.com/cgi-bin/qun_info/get_group_card",
                "uin=o3392295988;skey=MkmNZIj0BC;vkey=4jK7sn1201%3D%3D;sid=AUfOF8paAxI628FqSK08wrmh",
                "gc=57375224&u=591145360&bkn=7938746");
        //pgv_info=ssid=s4061823836; uin=o0591145360; skey=@hD3icxah7; pgv_si=s3267763200; _qpsvr_localtk=0.5380415238195423; pgv_pvid=123770404; pgv_pvi=336371712; pt2gguin=o0591145360; ptisp=ctc; RK=IEFm/3KuVK; ptcz=498b31e47a96d0755db4eed1103e2a6ad9efcfc4a8067f61af285f7ac57665ba; zzpaneluin=; zzpanelkey=; p_skey=6xzlCQEV8fMpUFGGCo*twwvV-cKJhmA781mP8ykNp4I_; pt4_token=TwlIwUunzj2gtAu7zYqooHJjhp6FxFEfhnFeIjPBBF8_; p_uin=o0591145360
        //
        System.out.println(s);
        //s = sendPost("http://qinfo.clt.qq.com/cgi-bin/qun_info/get_group_members", "uin=o0591145360;skey=@hD3icxah7", "gc=" + "57375224" + "&bkn=" + "510087624");
        //System.out.println(s);

    }

    
    
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String Cokkie, String parm) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.22 Safari/537.36 SE 2.X MetaSr 1.0");
            conn.setRequestProperty("Cookie", Cokkie);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(parm);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String cokkie, String param) {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (cokkie != null && !cokkie.isEmpty()) {
                connection.setRequestProperty("Cookie", cokkie);
            }
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return sb.toString();
    }

    private class me {

    }
}
