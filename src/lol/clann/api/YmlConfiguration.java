/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.CharSet;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class YmlConfiguration {

    private JavaPlugin plugin;
    private FileConfiguration configuration = null;
    private File configurationFile = null;
    private String fileName;
    private FileInputStream fis = null;
    private FileOutputStream fos = null;
    private String path;

    public YmlConfiguration(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        reloadDataConfig();
    }

    public YmlConfiguration(String path) {
        this.path = path;
        reloadDataConfig();
    }

    public InputStream getInputStream() throws FileNotFoundException {
        fis = new FileInputStream(configurationFile);
        return fis;
    }

    public OutputStream getOutputStream() throws FileNotFoundException {
        fos = new FileOutputStream(configurationFile);
        return fos;
    }

    public void reloadDataConfig() {
        try {
            DataApi.close(fis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (configurationFile == null) {
            if (plugin != null) {
                configurationFile = new File(plugin.getDataFolder(), this.fileName);
            } else {
                configurationFile = new File(path);
            }
            if (!configurationFile.exists()) {
                //    plugin.log("新建playerdata.yml");
                try {
                    configurationFile.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
        if (plugin != null) {
            InputStream defInputStream = this.plugin.getResource(this.fileName);
            if (defInputStream != null) {
                InputStreamReader reader = null;
                reader = new InputStreamReader(defInputStream);
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
                this.configuration.setDefaults(defConfig);
            }
        }
    }

    public FileConfiguration getDataConfig() {
        if (this.configuration == null) {
            reloadDataConfig();
        }
        return this.configuration;
    }

    public void saveDataConfig() {
        try {
            DataApi.close(fis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ((this.configuration == null) || (this.configurationFile == null)) {
            return;
        }
        try {
            getDataConfig().save(this.configurationFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
