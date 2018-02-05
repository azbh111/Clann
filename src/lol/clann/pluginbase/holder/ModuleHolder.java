/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase.holder;

import java.util.*;
import lol.clann.Clann;
import lol.clann.object.command.CEException;
import lol.clann.pluginbase.*;

/**
 *
 * @author zyp
 */
public class ModuleHolder {

    //记录所有模块
    private final HashMap<String, Module> modules = new HashMap();

    public Module get(String s) {
        return modules.get(s);
    }

    public void add(Module o) {
        modules.put(o.getName(), o);
    }

    public void enableAll() {
        //不启用的模块
        List<String> re = new LinkedList();
        Iterator<Map.Entry<String, Module>> it = modules.entrySet().iterator();
        while (it.hasNext()) {
            Module m = it.next().getValue();
            ArrayList<String> not = enable(m);
            if (!not.isEmpty()) {
                Clann.log("模块" + m.getName() + "缺少依赖:" + not.toString() + ",放弃加载");
                it.remove();
            }
        }
    }

    /**
     * 根据依赖关系安全加载模块
     * 返回缺少的模块
     *
     * @param m
     *
     * @return
     */
    private ArrayList<String> enable(Module m) {
        ArrayList<String> re = new ArrayList();
        if (m.isEnable()) {
            return re;
        }
        ArrayList<String> dp = m.getDepend();
        if (dp.size() == 1) {
            m.enable();
            Clann.log("enable module:" + m.getName());
        } else {
            for (String s : dp) {
                Module d = modules.get(s);
                if (d == null) {
                    re.add(s);
                } else {
                    re.addAll(enable(d));
                }
            }
            if (re.isEmpty()) {
                m.enable();
                Clann.log("enable module:" + m.getName());
            }
        }
        return re;
    }

    /**
     * 根据依赖关系安全卸载模块
     *
     * @param m
     */
    private void disable(Module m) {
        if (!m.isEnable()) {
            return;//已停用
        }
        for (Module c : modules.values()) {
            if (c.getDepend().contains(m.getName())) {
                disable(c);//先关闭孩子
            }
        }
        m.disable();
        Clann.log("disable module:" + m.getName());
    }

    /**
     * 插件卸载时调用
     */
    public void disableAll() {
        BaseAPI.loopRemoveCollection(modules.values(), o -> disable(o));
    }
}
