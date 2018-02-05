package lol.clann;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import lol.clann.Utils.ObscureUtil;
import lol.clann.afk.AFKData;
import lol.clann.api.*;
import lol.clann.listener.InventoryClickInterval;
import lol.clann.afk.AFKListener;
import lol.clann.manager.ThreadManager;
import lol.clann.pluginbase.BasePlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Clann extends BasePlugin {

    public static Clann plugin = null;
    public static ServerTick serverTick = null;
    public static DateFormat dateFormate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss_SSS");
    public static File serverDir = new File(System.getProperty("user.dir"));//服务器根目录
    public static File parentServerDir = new File(System.getProperty("user.dir")).getParentFile();//服务器上级目录
    public static Map<String, String> lang = new HashMap();
    public boolean run = true;
//    public List<BukkitTask> tasks = new ArrayList<>();
    public pluginDebug debuger;
    public DataIO data;
    public AFKData afkdata;
    public File modLang_Lang = null;
    public File mcLang_Lang = null;

    public Clann() {
        plugin = this;
        modLang_Lang = new File(this.getDataFolder(), "modLanguage.lang");
        mcLang_Lang = new File(this.getDataFolder(), "mcLanguage.lang");
    }

    public void registerListener() {
        new AFKListener();
        new InventoryClickInterval();
        new ThreadManager();
    }

    public static Permission getPermission() {
        return perm;
    }

    private static Permission perm = null;

    private boolean setupPermissions() {
        try {
            Class permClass = Class.forName("net.milkbowl.vault.permission.Permission");
            RegisteredServiceProvider permissionProvider = getServer().getServicesManager().getRegistration(permClass);
            if (permissionProvider != null) {
                perm = (Permission) permissionProvider.getProvider();
            }
            return (perm != null);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected void reloadConfig0() {

    }

    @Override
    public void onDisable0() {
        run = false;
//        for (BukkitTask bt : tasks) {
//            bt.cancel();
//        }
    }

    @Override
    public void onEnable0() {
        try {
            if (!setupPermissions()) {
                log("权限系统加载失败");
                Bukkit.shutdown();
                return;
            }
//            saveDefaultConfig();
//            ObscureUtil.init();
//            ReflectApi.init();
//            serverTick = new ServerTick();
            debuger = new pluginDebug(this);
//            data = new DataIO(this);
            registerListener();

//            LanguageApi.init();
//            log("语言映射建立完毕");
//            new Command();
        } catch (Throwable ex) {
            ex.printStackTrace();
            log("初始化过程出错");
            Bukkit.shutdown();
        }
    }
}
