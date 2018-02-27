/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Administrator
 */
public class SystemApi {

    private static int cpuThreads = Runtime.getRuntime().availableProcessors();

    /**
     * 返回CPU线程数
     *
     * @return
     */
    public static int getCPUThreads() {
        return cpuThreads;
    }

    public static Charset getSystemCharset() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("win")) {
            return Charset.forName("GBK");
        } else {
            return Charset.forName("UTF-8");
        }
    }

    /**
     * 获得进程信息
     */
    public static List<String> getProcess() {
        Process proc;
        List<String> list = new ArrayList<>();
        try {
            Set<String> set = new HashSet<>();
            proc = Runtime.getRuntime().exec("tasklist");
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String info = br.readLine();
            while (info != null) {
                set.add(info);
                info = br.readLine();
            }
            list.addAll(set);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取CPUID
     *
     * @return
     */
    public String getCpuId() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());
            String property = sc.next();
            String serial = sc.next();
            return serial.toUpperCase();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * 获取指定盘符的硬盘序列号
     *
     * @param root 盘符
     *
     * @return
     */
    public static String getSerialNumber(String root) {
        String result = "";
        try {
            File file = File.createTempFile("damn", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                    + "Set colDrives = objFSO.Drives\n"
                    + "Set objDrive = colDrives.item(\""
                    + root
                    + "\")\n"
                    + "Wscript.Echo objDrive.SerialNumber"; // see note  
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim().toUpperCase();
    }

    /**
     * 驱动器卷序列号
     *
     * @param root 驱动器盘符
     *
     * @return
     */
    public static String getHdSerialInfo(String root) {
        String HdSerial = "";//记录硬盘序列号
        try {
            Process proces = Runtime.getRuntime().exec("cmd /c dir " + root + ":");//获取命令行参数
            BufferedReader buffreader = new BufferedReader(new InputStreamReader(proces.getInputStream()));
            buffreader.readLine();
            String[] s = buffreader.readLine().split(" ");
            HdSerial = s[s.length - 1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HdSerial;//返回硬盘序列号
    }

    public static String getDesktopPath() {
        return FileSystemView.getFileSystemView().getHomeDirectory().getPath();
    }

    /**
     * 从剪切板获得文字。
     */
    public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容  
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型  
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * 将字符串复制到剪切板。
     */
    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

    /**
     * 从剪切板获得图片。
     *
     * @return
     *
     * @throws java.lang.Exception
     */
    public static Image getImageFromClipboard() throws Exception {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);
        if (cc == null) {
            return null;
        } else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return (Image) cc.getTransferData(DataFlavor.imageFlavor);
        }
        return null;
    }

    /**
     * 复制图片到剪切板。
     */
    public static void setClipboardImage(final Image image) {
        Transferable trans = new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            public Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) {
                    return image;
                }
                throw new UnsupportedFlavorException(flavor);
            }

        };
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans,
                null);
    }
}
