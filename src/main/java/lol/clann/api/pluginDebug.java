/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import lol.clann.Clann;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class pluginDebug {

    JavaPlugin plugin;
    File log;
    FileWriter fw;
    StringBuilder sb;
    StringWriter sw;
    PrintWriter pw;

    public pluginDebug(JavaPlugin plugin)   {
        try {
            this.plugin = plugin;
            File f1 = new File(System.getProperty("user.dir") + File.separator + "debuginfo" + File.separator);
            if (!f1.exists()) {
                f1.mkdirs();
            }
            log = new File(System.getProperty("user.dir") + File.separator + "debuginfo" + File.separator + plugin.getName() + ".log");
            if (!log.exists()) {
                log.createNewFile();
            }
            fw = new FileWriter(log, true);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("["+plugin.getName()+"]Debug文件建立失败");
        }
    }

    public boolean log(Throwable e) {
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        
        sb = new StringBuilder();
        sb.append(Clann.dateFormate.format(new Date())).append("\n").append(sw.toString()).append("\n\n");
        String s = sb.toString();
        try {
            sw.close();
            fw.write(s);
            fw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public void log(String... s) {
        try {
            sb = new StringBuilder();
            for (String ss : s) {
                sb.append(ss);
            }
            fw.write(sb.toString());
            fw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
