/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase;

import java.io.File;
import lol.clann.pluginbase.BasePlugin;

/**
 *
 * @author zyp
 */
public abstract class ModuleConfiguration extends BaseConfiguration {

    public ModuleConfiguration(BasePlugin holder, String fileName) {
        this(holder, null, fileName);
    }

    public ModuleConfiguration(BasePlugin holder, String folder, String fileName) {
        super(holder, folder, fileName);
        reloadConfig();
    }

    /**
     * 重载模块
     */
    public final void reloadConfig() {
        super.reloadDataConfig();
        reloadConfig0();
    }

    /**
     * 子类应实现重载数据
     */
    protected abstract void reloadConfig0();

}
