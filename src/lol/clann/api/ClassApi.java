/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lol.clann.Utils.ReflectionUtils;
import lol.clann.object.Refection.RefClass;

public class ClassApi {

    public static RefClass[] warpRefClass(Object[] os) {
        RefClass[] ref = new RefClass[os.length];
        Object o;
        for (int i = 0; i < os.length; i++) {
            o = os[i];
            if (o instanceof RefClass) {
                ref[i] = (RefClass) o;
            } else if (o instanceof Class) {
                ref[i] = ReflectionUtils.getRefClass((Class) o);
            } else {
                ref[i] = ReflectionUtils.getRefClass(o.getClass());
            }
        }
        return ref;
    }

    public static Class[] warpClasses(Object... os) {
        Class[] cs = new Class[os.length];
        Object o;
        for (int i = 0; i < os.length; i++) {
            o = os[i];
            if (o instanceof RefClass) {
                cs[i] = ((RefClass) o).getRealClass();
            } else if (o instanceof Class) {
                cs[i] = (Class) o;
            } else {
                cs[i] = o.getClass();
            }
        }
        return cs;
    }

    public static RefClass warpRefClass(Object o) {
        if (o instanceof RefClass) {
            return (RefClass) o;
        } else if (o instanceof Class) {
            return ReflectionUtils.getRefClass((Class) o);
        } else {
            return ReflectionUtils.getRefClass(o.getClass());
        }
    }

    public static Class warpClass(Object o) {
        if (o instanceof RefClass) {
            return ((RefClass) o).getRealClass();
        }
        if (o instanceof Class) {
            return (Class) o;
        }
        return o.getClass();
    }

    /**
     * 获得类的字节码中的名字
     *
     * @param clazz
     * @return
     */
    public static String getNameForBytecode(Class clazz) {
        if (clazz.equals(void.class)) {
            return "V";
        } else if (clazz.isArray()) {
            return clazz.getName().replaceAll("\\.", "/");
        } else {
            return Array.newInstance(clazz, 1).getClass().getName().substring(1).replaceAll("\\.", "/");
        }
    }

    /**
     * 显示一个类的所有信息
     *
     * @param name
     * @param sort
     * @return 
     */
    public static List<String> showClassInfo(String name, boolean sort) {
        List<String> info = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        List<String> constructors = new ArrayList<>();
        List<String> classes = new ArrayList<>();
        Class<?> clazz = ReflectionUtils.getRefClass(name).getRealClass();
        //获取所有属性
        for (Field f : clazz.getFields()) {
            if (!fields.contains(f.toString())) {
                fields.add(f.toString());
            }
        }
        for (Field f : clazz.getDeclaredFields()) {
            if (!fields.contains(f.toString())) {
                fields.add(f.toString());
            }
        }
        if (sort) {
            Collections.sort(fields);
        }
        //获取所有方法
        for (Method m : clazz.getMethods()) {
            if (!methods.contains(m.toString())) {
                methods.add(m.toString());
            }
        }
        for (Method m : clazz.getDeclaredMethods()) {
            if (!methods.contains(m.toString())) {
                methods.add(m.toString());
            }
        }
        if (sort) {
            Collections.sort(methods);
        }
        //获取所有构造器
        for (Constructor c : clazz.getConstructors()) {
            if (!constructors.contains(c.toString())) {
                constructors.add(c.toString());
            }
        }
        for (Constructor c : clazz.getDeclaredConstructors()) {
            if (!constructors.contains(c.toString())) {
                constructors.add(c.toString());
            }
        }
        if (sort) {
            Collections.sort(constructors);
        }
        //获取所有内部类
        for (Class<?> c : clazz.getClasses()) {
            if (!classes.contains(c.toString())) {
                classes.add(c.toString());
            }
        }
        for (Class<?> c : clazz.getDeclaredClasses()) {
            if (!classes.contains(c.toString())) {
                classes.add(c.toString());
            }
        }
        if (sort) {
            Collections.sort(classes);
        }
        info.add("类" + clazz.toString() + "信息");
        info.add("=================属性=================");
        info.addAll(fields);
        info.add("=================构造方法=============");
        info.addAll(constructors);
        info.add("=================方法=================");
        info.addAll(methods);
        info.add("=================内部类===============");
        info.addAll(classes);
        return info;
    }

    /**
     * 返回Class 对象表示的类声明的所有构造方法。
     */
    public static Constructor<?>[] getDeclaredConstructors(Class clazz) throws Exception {
        return clazz.getDeclaredConstructors();
    }

    /**
     * 返回Class 对象所表示的类的所有公共构造方法。
     */
    public static Constructor<?>[] getConstructors(Class clazz) throws Exception {
        return clazz.getConstructors();
    }

    /**
     * 返回Class 对象所表示的类或接口（包括那些由该类或接口声明的以及从超类和超接口继承的那些的类或接口）的公共 成员 方法
     */
    public static Method[] getMethods(Class clazz) {
        return clazz.getMethods();
    }

    /**
     * 返回Class 对象表示的类或接口声明的所有方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法。
     */
    public static Method[] getDeclaredMethods(Class clazz) {
        return clazz.getDeclaredMethods();
    }

    /**
     * 返回 Class 对象所表示的类或接口的所有可访问公共字段
     */
    public static Field[] getFields(Class clazz) {
        return clazz.getFields();
    }

    public static Field getFieldByReturnType() {
        return null;
    }

    /**
     * 返回Class 对象所表示的类或接口所声明的所有字段
     */
    public static Field[] getDeclaredFields(Class clazz) {
        return clazz.getDeclaredFields();

    }

    /**
     * 获取类中指定属性
     */
    public static Field getField(Class clazz, String name) throws NoSuchFieldException {
        NoSuchFieldException e = null;
        Field f = null;
        try {
            //尝试直接获取属性
            f = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e1) {
            try {
                f = clazz.getField(name);
            } catch (NoSuchFieldException e2) {
                //通过枚举获得可访问的公共属性
                Field[] fs = getFields(clazz);
                for (Field ff : fs) {
                    if (ff.getName().equals(name)) {
                        f = ff;
                        break;
                    }
                }
                //通过枚举获得所有属性
                if (f == null) {
                    fs = getDeclaredFields(clazz);
                    for (Field ff : fs) {
                        if (ff.getName().equals(name)) {
                            f = ff;
                            break;
                        }
                    }
                }
                e = e2;
            }
        }
        if (f != null) {
            f.setAccessible(true);
            return f;
        } else {
            throw e;
        }
    }

    /**
     * 获取指定对象指定属性的值
     */
    public static Object getValue(Field filed, Object o) throws IllegalArgumentException, IllegalAccessException {
        return filed.get(o);
    }

    /**
     * 通过名字获取类中的方法
     */
    public static Method getMethod(Class<?> clazz, String mname) throws NoSuchMethodException {
        Method m = null;
        NoSuchMethodException e = null;
        try {
            m = clazz.getDeclaredMethod(mname);
        } catch (NoSuchMethodException e1) {
            try {
                m = clazz.getMethod(mname);
            } catch (NoSuchMethodException e2) {
                for (Method me : clazz.getDeclaredMethods()) {
                    if (me.getName().equals(mname)) {
                        m = me;
                    }
                    break;
                }
                if (m == null) {
                    for (Method me : clazz.getMethods()) {
                        if (me.getName().equals(mname)) {
                            m = me;
                        }
                        break;
                    }
                }
                e = e2;
            }
        }
        if (m != null) {
            m.setAccessible(true);
            return m;
        } else {
            throw e;
        }
    }

    /**
     * 通过名字和参数列表获取类中的方法
     */
    public static Method getMethod(Class<?> clazz, String mname, Class<?>... args) throws NoSuchMethodException {
        Method m = null;
        NoSuchMethodException e = null;
        try {
            m = clazz.getDeclaredMethod(mname, args);
        } catch (NoSuchMethodException e1) {
            try {
                m = clazz.getMethod(mname, args);
            } catch (NoSuchMethodException e2) {
                for (Method me : clazz.getDeclaredMethods()) {
                    if (me.getName().equals(mname)) {
                        if (isSameParameters(me.getParameterTypes(), args)) {
                            m = me;
                            break;
                        }
                    }
                }
                if (m == null) {
                    for (Method me : clazz.getMethods()) {
                        if (me.getName().equals(mname)) {
                            if (isSameParameters(me.getParameterTypes(), args)) {
                                m = me;
                                break;
                            }
                        }
                    }
                }
                e = e2;
            }
        }
        if (m != null) {
            m.setAccessible(true);
        } else {
            throw e;
        }
        return m;
    }

    /**
     * 通过参数列表获取类中的方法
     */
    public static Method getMethod(Class<?> clazz, Class<?>... args) throws NoSuchMethodException {
        Method m = null;
        for (Method me : clazz.getDeclaredMethods()) {
            if (isSameParameters(me.getParameterTypes(), args)) {
                m = me;
                break;
            }
        }
        if (m == null) {
            for (Method me : clazz.getMethods()) {
                if (isSameParameters(me.getParameterTypes(), args)) {
                    m = me;
                    break;
                }
            }
        }
        if (m != null) {
            m.setAccessible(true);
            return m;
        } else {
            //内找到方法，人为构造一个NoSuchMethodException并抛出
            try {
                Method mm = clazz.getMethod("未找到指定参数的方法，人为构造异常并抛出", args);
                return null;
            } catch (NoSuchMethodException e) {
                throw e;
            }
        }
    }

    /**
     * 通过参数列表获取类中的构造方法
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws NoSuchMethodException {
        Constructor<?> c = null;
        NoSuchMethodException e = null;
        try {
            c = clazz.getConstructor(args);
        } catch (NoSuchMethodException e1) {
            Constructor[] constructors = clazz.getConstructors();
            for (Constructor cc : constructors) {
                if (isSameParameters(cc.getParameterTypes(), args)) {
                    c = cc;
                    break;
                }
            }
            e = e1;
        }
        if (c != null) {
            c.setAccessible(true);
            return c;
        } else {
            throw e;
        }
    }

    /**
     * 判断参数列表是否相同
     */
    public static Boolean isSameParameters(Class<?>[] cs, Class<?>... args) {
        if (cs.length != args.length) {
            return false;
        }
        Boolean same = true;
        for (int i = 0; i < cs.length; i++) {
            if (!isSameClazz(cs[i], args[i])) {
                same = false;
                break;
            }
        }
        return same;
    }

    /**
     * 获得对象Object中的属性name
     */
    public static Object getObject(Object obj, String name) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        return getValue(getField(obj.getClass(), name), obj);
    }

    /**
     * 设置对象obj中的属性name值为value
     */
    public static void setObject(Object obj, String name, Object value) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        getField(obj.getClass(), name).set(obj, value);
    }

    /**
     * 调用obj中的 无参 方法method invokeMethod(Object 对象, String 方法名)
     */
    public static Object invokeMethod(Object obj, String method) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getMethod(obj.getClass(), method).invoke(obj, new Object[]{});
    }

    /**
     * 根据 名称和参数 调用类中的方法 invokeMethod(Class<?> 类, String 方法名, Class<?>[]
     * 参数类型列表,Object 对象, Object... 参数列表)
     */
    public static Object invokeMethod(Class<?> clazz, String method, Class<?>[] args, Object obj, Object... initargs) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getMethod(clazz, method, args).invoke(obj, initargs);
    }

    /**
     * 根据 名称 调用类中的方法 invokeMethod(Class<?> 类, String 方法名,Object 对象, Object...
     * 参数列表)
     */
    public static Object invokeMethod(Class<?> clazz, String method, Object obj, Object... initargs) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getMethod(clazz, method).invoke(obj, initargs);
    }

    /**
     * 根据 名称 调用类中的方法 invokeMethod(String 方法名,Object 对象, Object[] 参数列表)
     */
    public static Object invokeMethod(String method, Object obj, Object... initargs) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getMethod(obj.getClass(), method).invoke(obj, initargs);
    }

    /**
     * 根据参数类型列表实例化对象 invokeConstructor(Class<?> 类, Class<?>[] 参数类型列表, Object...
     * 参数)
     */
    public static Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initargs) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getConstructor(clazz, args).newInstance(initargs);
    }

    /**
     * 调用无参构造函数生成对象
     */
    public static Object invokeConstructor(Class<?> clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return invokeConstructor(clazz, new Class[]{}, new Object[]{});
    }

    /**
     * 判断是否为两个相同的类
     */
    public static boolean isSameClazz(Class<?> pClazz1, Class<?> pClazz2) {
        if (pClazz1 == pClazz2) {
            return true;
        }
        return ((pClazz1 == Void.TYPE) && (pClazz2 == null)) || ((pClazz1 == null) && (pClazz2 == Void.TYPE));
    }
}
