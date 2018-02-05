/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lol.clann.Clann;
import lol.clann.ClannAPI;
import static lol.clann.Utils.ReflectionUtils.*;
import lol.clann.object.Refection.MethodCondition;
import lol.clann.object.Refection.RefClass;
import lol.clann.object.Refection.RefField;
import lol.clann.object.Refection.RefMethod;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Administrator
 */
public class LanguageApi {

    public static Map<String, String> lang = new HashMap();
    static{
        try {
            init();
            ClannAPI.log("语言映射建立完毕");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private static void init() throws IOException {
        if (!Clann.plugin.mcLang_Lang.exists()) {
            me.loadMCLanguage(Clann.plugin.mcLang_Lang);
        }
        Map<String, String> mcLang = me.load(Clann.plugin.mcLang_Lang);
        Clann.lang.putAll(mcLang);
        ClannAPI.log("读取" + mcLang.size() + "条语言映射");
        if (!Clann.plugin.modLang_Lang.exists()) {
            me.loadModLanguage(Clann.plugin.modLang_Lang);
        }
        Map<String, String> modLang = me.load(Clann.plugin.modLang_Lang);
        Clann.lang.putAll(modLang);
        ClannAPI.log("读取" + modLang.size() + "条语言映射");
        ClannAPI.log("一共" + Clann.lang.size() + "条语言映射");
        me.refreshStringTranslate();
    }

    public static void reload(File lang) throws IOException {
        init();
    }

    public static String translate(String key) {
        String s = lang.get(key);
        if (s != null) {
            return s;
        } else {
            s = Clann.lang.get(key);
            if (s != null) {
                lang.put(key, s);
                return s;
            } else if (key.endsWith(".name")) {
                s = me._translate(key.replaceAll("\\.name$", ""));
            } else {
                s = me._translate(key + ".name");
            }
            if (s == null) {
                lang.put(key, "");
                return "";
            } else {
                return s;
            }
        }
    }

    /**
     * 获取物品显示名字,不包括自定义名字
     * @param item
     * @return 
     */
    public static String getItemStackDisplayName(ItemStack item) {
        Object o;
        if (ReflectApi.CraftItemStack.isInstance(item)) {
            o = ReflectApi.CraftItemStack_handle.of(item).get();
        } else {
            o = ReflectApi.CraftItemStack_asNMSCopy.call(item);
        }
        return ReflectApi.Item_getItemStackDisplayName.of(ReflectApi.ItemStack_item.of(o).get()).call(o);
    }

    /**
     * 获取显示名字
     * @param item
     * @return 
     */
    public static String getDisplayName(ItemStack item) {
        if (ReflectApi.CraftItemStack.isInstance(item)) {
            return ReflectApi.ItemStack_getDisplayName.of(ReflectApi.CraftItemStack_handle.of(item).get()).call();
        } else {
            return ReflectApi.ItemStack_getDisplayName.of(ReflectApi.CraftItemStack_asNMSCopy.call(item)).call();
        }
    }

    static class me {

        private static String _translate(String key) {
            String s = lang.get(key);
            if (s != null) {
                return s;
            } else {
                s = Clann.lang.get(key);
                if (s != null) {
                    lang.put(key, s);
                    return s;
                } else {
                    return null;
                }
            }
        }

        /**
         * 重新生成MOD语言文件
         *
         * @throws IOException
         */
        private static void loadModLanguage(File lang) throws IOException {
            RefClass _LanguageRegistry = getRefClass("cpw.mods.fml.common.registry.LanguageRegistry");
            RefMethod _instance = _LanguageRegistry.findMethod(new MethodCondition().withReturnType(_LanguageRegistry.getRealClass()).withTypes());
            RefField<Map<String, Properties>> _LanguageRegistry_modLanguageData = _LanguageRegistry.getField("modLanguageData");
            Object instance = _instance.of(null).call();
            Map<String, Properties> map = _LanguageRegistry_modLanguageData.of(instance).get();
            Properties en_US = map.get("en_US");
            Properties zh_CN = map.get("zh_CN");
            Properties lang_Properties = new Properties();
            if (en_US != null) {
                lang_Properties.putAll(en_US);
            }
            if (zh_CN != null) {
                lang_Properties.putAll(zh_CN);
            }
            if (!lang.exists()) {
                lang.createNewFile();
            }
            save(lang_Properties, lang);
        }

        /**
         * 生成MC语言文件
         *
         * @param lang
         * @throws IOException
         */
        private static void loadMCLanguage(File lang) throws IOException {
            if (!lang.exists()) {
                lang.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(lang);
            InputStream is = Clann.class.getResourceAsStream("/mcLanguage.lang");
            DataApi.transfer(is, fos, 8192);
            DataApi.close(fos, is);
        }

        /**
         * 刷新net.minecraft.util.StringTranslate.class
         */
        private static void refreshStringTranslate() {
            RefClass StringTranslate = getRefClass("net.minecraft.util.StringTranslate");
            RefMethod getInstance = StringTranslate.findMethodByReturnType(StringTranslate.getRealClass());
            RefField<Map> map = StringTranslate.findField(Map.class);
            Map m = map.of(getInstance.of(null).call()).get();
            int n1 = m.size();
            m.putAll(Clann.lang);
            int n2 = m.size();
            ClannAPI.log("更改" + (n1 + Clann.lang.size() - n2) + "条语言映射");
            ClannAPI.log("添加" + (n2 - n1) + "条语言映射");
        }

        private static Map<String, String> load(File lang) throws IOException {
            boolean save = false;
            Map<String, String> map = new HashMap();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(lang), "UTF-8"));
            String s;
            while ((s = br.readLine()) != null) {
                if (!s.startsWith("#")) {
                    String[] ss = s.split("=", 2);
                    if (ss.length != 2) {
                        ClannAPI.log("错误语言节点:" + s);
                        save = true;
                    } else {
                        map.put(ss[0], ss[1]);
                    }
                }
            }
            br.close();
            if (save) {
                save(map, lang);
            }
            return map;
        }

        private static void save(Map map, File lang) throws IOException {
            StringBuilder sb = new StringBuilder();
            List<String> keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                sb.append(key).append("=").append(map.get(key)).append("\r\n");
            }
            FileOutputStream fos = new FileOutputStream(lang);
            fos.write(sb.toString().getBytes("UTF-8"));
            fos.close();
        }
    }
}
