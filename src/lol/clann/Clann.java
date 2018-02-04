package lol.clann;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lol.clann.Utils.ObscureUtil;
import lol.clann.afk.AFKData;
import lol.clann.api.*;
import lol.clann.listener.InventoryClickInterval;
import lol.clann.afk.AFKListener;
import lol.clann.manager.ThreadManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Clann extends JavaPlugin {

    public static Clann plugin = null;
    public static ServerTick serverTick = null;
    public static DateFormat dateFormate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss_SSS");
    public static File serverDir = new File(System.getProperty("user.dir"));//服务器根目录
    public static File parentServerDir = new File(System.getProperty("user.dir")).getParentFile();//服务器上级目录
    public static Map<String, String> lang = new HashMap();
    public boolean run = true;
    public List<BukkitTask> tasks = new ArrayList<>();
    public pluginDebug debuger;
    public DataIO data;
    public AFKData afkdata;
    public File modLang_Lang = null;
    public File mcLang_Lang = null;
    private static final boolean startUp;
    private static Logger _logger;
//    public Clann() {
//
//    }
    static {
        Properties serverProperties = new Properties();
        try {
            File f = new File("server.properties");
            InputStream is = new FileInputStream(f);
            serverProperties.load(is);
            is.close();
        } catch (Exception e) {
        }
        startUp = serverProperties.getProperty("server-name", "").equals("自由梦幻");
    }

    @Override
    public void onEnable() {
        if (startUp) {
            try {
                plugin = this;
                _logger = getLogger();
                if (!setupPermissions()) {
                    log("权限系统加载失败");
                    Bukkit.shutdown();
                    return;
                }
                saveDefaultConfig();
                ObscureUtil.init();
                ReflectApi.init();
                serverTick = new ServerTick();
                debuger = new pluginDebug(this);
                afkdata = new AFKData(plugin);
                data = new DataIO(this);
                registerListener();
                modLang_Lang = new File(this.getDataFolder(), "modLanguage.lang");
                mcLang_Lang = new File(this.getDataFolder(), "mcLanguage.lang");
                LanguageApi.init();
                log("语言映射建立完毕");
                new Command();
            } catch (Throwable ex) {
                ex.printStackTrace();
                log("初始化过程出错");
                Bukkit.shutdown();
            }
        }
    }

    @Override
    public void onDisable() {
        serverTick.cancel();
        run = false;
        for (BukkitTask bt : tasks) {
            bt.cancel();
        }
    }

    public void registerListener() {
        
        Bukkit.getPluginManager().registerEvents(new AFKListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickInterval(), this);
        Bukkit.getPluginManager().registerEvents(new ThreadManager(), this);
    }

    public static void log(String s) {
        _logger.log(Level.INFO, s);
    }   
    public static void logError(String s) {
        _logger.log(Level.SEVERE, s);
    } 
    public static void logWarning(String s) {
        _logger.log(Level.WARNING, s);
    } 
    
    public static boolean startUp() {
        return startUp;
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
}
