/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.lang.annotation.*;
import java.util.*;
import lol.clann.Utils.PackageScanner;

import org.bukkit.plugin.Plugin;

/**
 * 包扫描工具类 实现指定注解的类自动实例化
 *
 * @author zyp
 */
public class AutoRegister {

    /**
     * 插件缓存
     */
    private static final Set<String> plugins = new HashSet();
    /**
     * 类缓存
     */
    private static final Set<String> classes = new HashSet();

    /**
     * 自动实例化指定插件指定类型的类
     *
     * @param plg 插件
     * @param type 类型，给null时实例化默认类型
     * @param params 参数列表
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static synchronized void register(Plugin plg, String type, Object... params) throws Exception {
        if (type == null) {
            type = "auto";
        }
        if (!plugins.contains(plg.getName())) {
            classes.addAll(PackageScanner.Scann(plg));
        }
        List<Class> list = new LinkedList();
        Class clazz;
        Register anno;
        Iterator<String> it = classes.iterator();
        while (it.hasNext()) {
            clazz = Class.forName(it.next());
            anno = (Register) clazz.getAnnotation(Register.class);
            if (anno != null && anno.plugin().equals(plg.getName()) && anno.type().equals(type)) {
                list.add(clazz);
            }
        }
        //按优先级排序
        Collections.sort(list, new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                return ((Register) o2.getAnnotation(Register.class)).priority() - ((Register) o1.getAnnotation(Register.class)).priority();
            }
        });
//        System.out.println("排序后");
//        for (Class c : list) {
//            System.out.println(((Register) c.getAnnotation(Register.class)).priority() + "  " + c.getName());
//        }
        //初始化
        for (Class c : list) {
            if (params != null && params.length > 0) {
                c.getDeclaredConstructor(ClassApi.warpClasses(params)).newInstance(params);
            } else {
                c.newInstance();
            }
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Register {

        /**
         * 所属插件的名字
         *
         * @return
         */
        String plugin();

        /**
         * 优先级，默认为0，越大优先加载
         *
         * @return
         */
        int priority() default 0;

        String type() default "auto";
    }
}
