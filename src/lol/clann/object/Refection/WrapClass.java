/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.Refection;

import java.lang.reflect.*;
import java.util.*;

/**
 * 对反射进行包装,便于快速调用反射方法
 *
 * @author zyp
 * @param <T>
 */
public class WrapClass<T> {

    Map<String, Method[]> methods = new HashMap();
    Map<String, Field> fields = new HashMap();
    List<Constructor<T>> constructors = new ArrayList();
    Class<T> clazz;

    public WrapClass(Class<T> clazz) {
        this.clazz = clazz;
        for (Method m : clazz.getDeclaredMethods()) {
            addMethod(m);
        }
        for (Method m : clazz.getMethods()) {
            addMethod(m);
        }
        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            fields.put(f.getName(), f);
        }
        for (Field f : clazz.getFields()) {
            if (!fields.containsKey(f.getName())) {
                f.setAccessible(true);
                fields.put(f.getName(), f);
            }
        }
        for (Constructor c : clazz.getDeclaredConstructors()) {
            c.setAccessible(true);
            constructors.add(c);
        }
    }

    public Class<T> getRealClass() {
        return clazz;
    }

    public T newInstance(Object... params) throws Exception {
        Constructor c = null;
        for (int i = 0; i < constructors.size(); i++) {
            c = constructors.get(i);
            if (check(c.getParameterTypes(), params)) {
                return (T) c.newInstance(params);
            }
        }
        throw new RuntimeException("No Constructor find:(" + paramsToClassString(params) + ")");
    }

    public void set(Object handle, String name, Object value) throws Exception {
        Field f = fields.get(name);
        if (f == null) {
            throw new RuntimeException("No Field find:" + name);
        }
        f.set(handle, value);
    }

    /**
     * 获取属性
     */
    public Object get(Object handle, String name) throws Exception {
        Field f = getField(name);
        return f.get(handle);
    }

    /**
     * 根据类别返回属性
     *
     * @param clazz
     *
     * @return
     */
    public Field getField(Class clazz) {
        Field re = null;
        for (Field f : fields.values()) {
            if (f.getType() == clazz) {
                if (re == null) {
                    re = f;
                } else {
                    throw new RuntimeException("One more fields that type are " + clazz.getName() + " find");
                }
            }
        }
        if (re == null) {
            throw new RuntimeException("No Field find: type=" + clazz.getName());
        }
        return re;
    }

    /**
     * 根据名称返回属性
     *
     * @param name
     *
     * @return
     */
    public Field getField(String name) {
        Field f = fields.get(name);
        if (f == null) {
            throw new RuntimeException("No Field find: name=" + name);
        }
        return f;
    }

    /**
     * 根据名字和参数返回方法
     *
     * @param name
     * @param params
     *
     * @return
     */
    public Method getMethod(String name, Object... params) {
        Method[] ms = methods.get(name);
        Method m = null;
        for (Method tm : ms) {
            if (check(tm.getParameterTypes(), params)) {
                return tm;
            }
        }
        throw new RuntimeException("No Such Method find:" + name + "(" + paramsToClassString(params) + ")");
    }

    /**
     * 执行方法
     *
     * @param name 方法名
     * @param handle 对象,静态方法handle传入null
     * @param params 参数列表
     *
     * @return
     */
    public Object invoke(Object handle, String name, Object... params) throws Exception {
        Method m = getMethod(name, params);
        return m.invoke(handle, params);
    }

    private String paramsToClassString(Object... params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            if (params[i] == null) {
                sb.append("null");
            } else {
                sb.append(params[i].getClass().getName());
            }
        }
        return sb.toString();
    }

    private boolean check(Class[] clazzes, Object... params) {
        if (clazzes.length != params.length) {
            return false;
        }
        //检验参数
        for (int i = 0; i < clazzes.length; i++) {
            if (!canCast(clazzes[i], params[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * byte 1
     * short 2
     * char、int 3
     * long 4
     * float 5
     * double 6
     * 小的可以转换为大的
     *
     * @return
     */
    private int getNumberLevel(Class clazz) {
        if (clazz == Byte.class || clazz == byte.class) {
            return 1;
        } else if (clazz == Short.class || clazz == Short.class) {
            return 2;
        } else if (clazz == Integer.class || clazz == int.class) {
            return 3;
        } else if (clazz == Long.class || clazz == long.class) {
            return 4;
        } else if (clazz == Float.class || clazz == float.class) {
            return 5;
        } else if (clazz == Double.class || clazz == double.class) {
            return 6;
        }
        return Integer.MAX_VALUE;
    }

    private boolean isNumber(Class clazz) {
        return clazz == byte.class || clazz == short.class || clazz == int.class || clazz == long.class || clazz == float.class || clazz == double.class;
    }

    /**
     * 判断clazz能否接收参数o
     *
     * @param clazz
     * @param o
     *
     * @return
     */
    private boolean canCast(Class clazz, Object o) {
        Class tClazz;
        if (o instanceof Class) {
            tClazz = (Class) o;
        } else {
            tClazz = o.getClass();
        }
        if (clazz == tClazz) {
            return true;
        }
        if (clazz.isAssignableFrom(tClazz)) {
            return true;//父类
        }
        if (isNumber(clazz)) {//clazz必须是基本数据类型,o才能转换
            int level = getNumberLevel(tClazz);
            if (level == Integer.MAX_VALUE) {
                if (tClazz == char.class) {
                    level = 3;
                }
            }
            if (level <= getNumberLevel(clazz)) {
                return true;
            }
        } else {
            if (clazz == char.class && tClazz == Character.class) {
                return true;
            }
            if (clazz == Character.class && tClazz == char.class) {
                return true;
            }
        }
        return false;
    }

    private void addMethod(Method add) {
        add.setAccessible(true);
        Method[] ms = methods.get(add.getName());
        if (ms == null) {
            methods.put(add.getName(), new Method[]{add});
        } else {
            Method[] newMS = new Method[ms.length + 1];
            System.arraycopy(ms, 0, newMS, 0, ms.length);
            newMS[ms.length] = add;
            methods.put(add.getName(), newMS);
        }
    }
}
