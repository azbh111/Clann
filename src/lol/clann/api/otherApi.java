/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
import lol.clann.Clann;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public class otherApi {

    
    
    /**
     * 通过InteractEvent获取放置方块的位置 InteractEvent先于BlockPlaceEvent触发
     * 直接判断BlockPlaceEvent可能会导致刷物品BUG，如领地刷UU，刷凋零等
     */
    public static Location getLocationByBlockFace(Block block, BlockFace face) {
        Location loc = block.getLocation();
        if (face.equals(BlockFace.UP)) {
            loc = loc.add(0, 1, 0);
        } else if (face.equals(BlockFace.DOWN)) {
            loc = loc.add(0, -1, 0);
        } else if (face.equals(BlockFace.WEST)) {
            loc = loc.add(-1, 0, 0);
        } else if (face.equals(BlockFace.EAST)) {
            loc = loc.add(1, 0, 0);
        } else if (face.equals(BlockFace.SOUTH)) {
            loc = loc.add(0, 0, 1);
        } else if (face.equals(BlockFace.NORTH)) {
            loc = loc.add(0, 0, -1);
        }

        return loc;
    }

    /**
     * 对插件进行授权检查，未获得授权的插件将无法加载
     *
     * @param plugin
     * @param <error>
     */
    public static void security(JavaPlugin plugin) {
        try {
            //生成机器唯一标识
            String disk = getMotherboardSN();
            String serial = disk;
            String qqqqqqqqq = "";
            char[] sssss = serial.toCharArray();
            for (char c : sssss) {
                Long llllll = (long) ((c + 99) * 111);
                qqqqqqqqq = qqqqqqqqq + ((((llllll >>> 1) << 3) * 9 + 11111) >> 1);
            }
            serial = qqqqqqqqq;
            //生成授权文件
            String serverPath = new File(System.getProperty("user.dir")).getPath();
            File file = new File(serverPath, "授权.dat");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);
            fw.write("|" + serial + "|" + "\r\n");
            fw.flush();
            fw.close();

            if (plugin.getName().equals("Clann")) {
                //为授权插件
                //不进行认证
                return;
            }

            boolean stop = true;
            Class<?> clazz = Class.forName("lol.clann.Clann");
            Field f = ClassApi.getField(clazz, "fdsfdsf");
            Clann clann = (Clann) Bukkit.getServer().getPluginManager().getPlugin("Clann");
            List<security> sfdsf324fds = (List<security>) f.get(clann);
            for (security fdsfdsf : sfdsf324fds) {
                if (plugin.getName().equals(fdsfdsf.getPlugin()) && serial.equals(fdsfdsf.getKey())) {
                    stop = false;
                    break;
                }
            }
            if (stop) {
                plugin.getLogger().info("[" + plugin.getName() + "]" + "未获得授权，无法加载插件");
                plugin.getLogger().info("[" + plugin.getName() + "]" + "授权文件已在服务器根目录生成，请发给作者申请授权QQ591145360");
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            } else {
                return;
            }
        } catch (Exception e) {
            plugin.getLogger().info("[" + plugin.getName() + "]" + "授权检查异常，无法加载插件,请将报错信息反馈给作者QQ591145360");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    private static String getMACAddress() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            //下面代码是把mac地址拼装成String
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                //mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            //把字符串所有小写字母改为大写成为正规的mac地址并返回
            return sb.toString().toUpperCase();
        } catch (UnknownHostException ex) {
            return null;
        } catch (SocketException ex) {
            return null;
        }
    }

    /**
     * 获取CPU序列号
     *
     * @return
     */
    public static String getCpuId() {
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
     * 获取主板序列号
     *
     * @return
     */
    public static String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));
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
     * 获取硬盘序列号getSerialNumber(String 盘符)
     *
     * @param root
     * @return
     */
    public static String getSerialNumber(String root) {
        String drive = root;
        String result = "";
        try {
            File file = File.createTempFile("damn", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                    + "Set colDrives = objFSO.Drives\n"
                    + "Set objDrive = colDrives.item(\""
                    + drive
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
        return result.trim();
    }
}
