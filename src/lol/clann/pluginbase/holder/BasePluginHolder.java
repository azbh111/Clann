/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase.holder;

import java.util.*;
import lol.clann.pluginbase.BaseAPI;
import lol.clann.pluginbase.BasePlugin;

/**
 *
 * @author zyp
 */
public class BasePluginHolder {
    private static final Map<String,BasePlugin> pluginMap = new HashMap();
    public static void add(BasePlugin p){
        if(pluginMap.containsKey(p.getName())){
            BaseAPI.mustTrue(false, "插件重"+p.getName()+"复加载");
        }else{
            pluginMap.put(p.getName(), p);
        }
    }
    public static BasePlugin get(String name){
        return pluginMap.get(name);
    }
}
