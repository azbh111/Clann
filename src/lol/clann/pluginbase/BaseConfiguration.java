/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase;

import java.io.*;
import lol.clann.pluginbase.BaseAPI;
import lol.clann.pluginbase.BasePlugin;
import lol.clann.pluginbase.api.Configable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Administrator
 */
public class BaseConfiguration {

    private FileConfiguration configuration = null;
    private File configurationFile = null;
    
    public BaseConfiguration(BasePlugin holder, String fileName) {
        this(holder, null, fileName);
    }

    public BaseConfiguration(BasePlugin holder, String folder, String fileName) {
        if (folder == null) {
            configurationFile = new File(holder.getDataFolder(), fileName);
        } else {
            File dir = new File(holder.getDataFolder(),folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            configurationFile = new File(dir, fileName);
        }
        if (!configurationFile.exists()) {
            BaseAPI.saveResource(holder, fileName, configurationFile.getParentFile(), true);
        }
        reloadDataConfig();
    }

    protected void reloadDataConfig() {
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    public FileConfiguration getDataConfig() {
        return configuration;
    }

    public void saveDataConfig() {
        try {
            getDataConfig().save(configurationFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
