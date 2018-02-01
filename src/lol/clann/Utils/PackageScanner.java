/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lol.clann.Clann;
import lol.clann.api.FileApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * 工具类，用于扫描指定包下的所有类
 *
 * @author zyp
 */
public class PackageScanner {

    /**
     * 包名
     */
    private String pkgName;

    /**
     * 包对应的路径名
     */
    private String pkgPath;

    private ClassLoader cl;

    private static final Set<String> classes = new HashSet();

    /**
     * 初始化
     */
    static {
        if (Bukkit.getServer() != null) {
            //扫描所有插件
            File dir = new File(Clann.plugin.getDataFolder().getParent());
            List<String> all = FileApi.FileList(dir);
            List<File> fs = new LinkedList();
            all.forEach(s -> {
                File f = new File(s);
                if (f.getPath().endsWith(".jar")) {
                    fs.add(f);
                }
            });
            fs.forEach(p -> {
                try {
                    classes.addAll(scanJar(p));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }

    }

    private static List<String> scanJar(File f) throws IOException {
        JarFile jar = new JarFile(f);
        List<String> classNameList = new LinkedList<>();
        Enumeration<JarEntry> entries = jar.entries();
        String name;
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            name = entry.getName();
            if (name.endsWith(ResourceType.CLASS_FILE.getTypeString())) {
                name = pathToPackage(trimSuffix(name));
                classNameList.add(name);
            }
        }
        return classNameList;
    }

    /**
     * 扫描指包下所有类
     *
     * @param pkgName
     *
     * @return
     */
    public static List<String> Scann(String pkgName) {
        List<String> re = new LinkedList();
        //扫描插件
        classes.forEach(p -> {
            if (p.startsWith(pkgName)) {
                re.add(p);
            }
        });
        try {
            //扫描
            re.addAll(new PackageScanner(pkgName).loadResource());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return re;
    }

    public static List<String> Scann(Plugin plugin) {
        String pkg = plugin.getClass().getPackage().getName();
        return Scann(pkg);
    }
//
//    private static List<String> Scann(PackageScanner scanner) {
//        List<String> list = null;
//        try {
//            list = scanner.loadResource();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        Clann.log("在包(" + scanner.pkgName + ")下扫描到" + list.size() + "个类");
//        return list;
//    }

    /**
     * 过滤,保留指定类/接口的子类/实现
     *
     * @param classList
     * @param _interface
     */
    public static void ParentFilter(Collection<String> classList, Class parent) throws ClassNotFoundException {
        Iterator<String> it = classList.iterator();
        String next;
        Class clazz;
        while (it.hasNext()) {
            next = it.next();
            clazz = Class.forName(next);
            if (!clazz.isInstance(parent)) {
                it.remove();
            }
        }
    }

    /**
     * 过滤掉没有指定注解的类
     *
     * @param classList
     * @param annotation
     *
     * @throws ClassNotFoundException
     */
    public static void AnnotationFilter(Collection<String> classList, Class annotation) throws ClassNotFoundException {
        Iterator<String> it = classList.iterator();
        String next;
        Class clazz;
        while (it.hasNext()) {
            next = it.next();
            clazz = Class.forName(next);
            if (!hasAnnotation(clazz, annotation)) {
                it.remove();
            }
        }
    }

    private PackageScanner(String pkgName) {
        this.pkgName = pkgName;
        this.pkgPath = packageToPath(pkgName);
        cl = Thread.currentThread().getContextClassLoader();
    }

    /**
     * 真扫描
     *
     * @return
     *
     * @throws IOException
     */
    private List<String> loadResource() throws IOException {
        List<String> list = new LinkedList();
        Enumeration<URL> urls = cl.getResources(pkgPath);
        String path;
        URL u;
        while (urls.hasMoreElements()) {
            u = urls.nextElement();
            switch (determineType(u)) {
                case JAR:
                    path = distillPathFromJarClassURL(u);
                    list.addAll(scanJar(path));
                    break;
                case FILE:
                    path = distillPathFromFileURL(u);
                    list.addAll(scanFile(path, pkgName));
                    break;
            }
        }
        return list;
    }

    /**
     * 根据URL判断是JAR包还是文件目录
     *
     * @param url
     *
     * @return
     */
    private ResourceType determineType(URL url) {
        if (url.getProtocol().equals(ResourceType.FILE.getTypeString())) {
            return ResourceType.FILE;
        }
        if (url.getProtocol().equals(ResourceType.JAR.getTypeString())) {
            return ResourceType.JAR;
        }
        throw new IllegalArgumentException("不支持该类型:" + url.getProtocol());
    }

    /**
     * 扫描JAR文件
     *
     * @param path
     *
     * @return
     *
     * @throws IOException
     */
    private List<String> scanJar(String path) throws IOException {
        JarFile jar = new JarFile(path);
        List<String> classNameList = new LinkedList<>();
        Enumeration<JarEntry> entries = jar.entries();
        String name;
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            name = entry.getName();
            if ((name.startsWith(pkgPath)) && (name.endsWith(ResourceType.CLASS_FILE.getTypeString()))) {
                name = pathToPackage(trimSuffix(name));
                classNameList.add(name);
            }
        }
        return classNameList;
    }

    /**
     * 扫描文件目录下的类
     *
     * @param path
     *
     * @return
     */
    private List<String> scanFile(String path, String basePkg) {
        File f = new File(path);
        List<String> classNameList = new LinkedList<>();
        // 得到目录下所有文件(目录)
        File[] files = f.listFiles();
        if (null != files) {
            int LEN = files.length;
            for (int ix = 0; ix < LEN; ++ix) {
                File file = files[ix];
                // 判断是否还是一个目录
                if (file.isDirectory()) {
                    // 递归遍历目录
                    List<String> list = scanFile(file.getAbsolutePath(), concat(basePkg, ".", file.getName()));
                    classNameList.addAll(list);
                } else if (file.getName().endsWith(ResourceType.CLASS_FILE.getTypeString())) {
                    // 如果是以.class结尾
                    String className = trimSuffix(file.getName());
                    // 命中
                    String result = concat(basePkg, ".", className);
                    classNameList.add(result);
                }
            }
        }
        return classNameList;
    }

    private static boolean hasAnnotation(Class clazz, Class ann) throws ClassNotFoundException {
        return clazz.getAnnotation(ann) != null;
    }

    /**
     * 把路径字符串转换为包名. a/b/c/d -> a.b.c.d
     *
     * @param path
     *
     * @return
     */
    public static String pathToPackage(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path.replaceAll("/", ".");
    }

    /**
     * 包名转换为路径名
     *
     * @param pkg
     *
     * @return
     */
    public static String packageToPath(String pkg) {

        return pkg.replace(".", File.separator);
    }

    /**
     * 将多个对象转换成字符串并连接起来
     *
     * @param objs
     *
     * @return
     */
    public static String concat(Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (int ix = 0; ix < objs.length; ++ix) {
            sb.append(objs[ix]);
        }
        return sb.toString();
    }

    /**
     * 去掉文件的后缀名
     *
     * @param name
     *
     * @return
     */
    public static String trimSuffix(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (-1 == dotIndex) {
            return name;
        }
        return name.substring(0, dotIndex);
    }

    /**
     * 文件RUL转换为文件路径
     *
     * @param u
     *
     * @return
     */
    public static String distillPathFromFileURL(URL u) {
        return new File(URI.create(u.toString())).getPath();
    }

    /**
     * JarClassURL转换为文件路径
     *
     * @param u
     *
     * @return
     */
    public static String distillPathFromJarClassURL(URL u) {
        String url = u.getPath();
        int endPos = url.lastIndexOf('!');
        String s = url.substring(0, endPos);
        return new File(URI.create(s)).getPath();

    }

    private enum ResourceType {
        JAR("jar"),
        FILE("file"),
        CLASS_FILE(".class");

        private String typeString;

        private ResourceType(String type) {
            this.typeString = type;
        }

        public String getTypeString() {
            return this.typeString;
        }
    }

}
