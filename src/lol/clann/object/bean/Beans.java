/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.bean;

import java.lang.reflect.*;
import java.util.*;
import lol.clann.Clann;
import lol.clann.Utils.PackageScanner;
import org.bukkit.craftbukkit.libs.com.google.gson.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 只允许基本数据类型、String和以上类型的数组
 *
 * @author zyp
 */
@BeansAnnotation
public abstract class Beans {

    /**
     * 存储所有Beans类的属性
     */
    private static final Map<Class, Map<String, Field>> classFields = new HashMap();
    /**
     * 存储不可实例化的Beans的选择器
     */
    private static final Map<Class, Field> selectors = new HashMap();

    private static final Field value;

    static {
        try {
            value = JsonPrimitive.class.getDeclaredField("value");
            value.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
        registerPackage("lol.clann.beans");
    }
    /**
     * 注册插件jar包里的bean
     * @param plg 
     */
    public static void registerPlugin(JavaPlugin plg){
       //注册所有Beans类
        List<String> classes = PackageScanner.Scann(plg);
        for (String s : classes) {
            try {
                Class clazz = Class.forName(s);
                if (clazz.isAnnotationPresent(BeansAnnotation.class)) {
                    registerClass(clazz);
                    Clann.logError("注册Bean失败:" + clazz.getName());
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 注册指定包下的Bean类
     *
     * @param pkg
     */
    public static void registerPackage(String pkg) {
        //注册所有Beans类
        List<String> classes = PackageScanner.Scann(pkg);
        for (String s : classes) {
            try {
                Class clazz = Class.forName(s);
                if (clazz.isAnnotationPresent(BeansAnnotation.class)) {
                    registerClass(clazz);
                    Clann.logError("注册Bean失败:" + clazz.getName());
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerClass(Class clazz) {
        Map<String, Field> fs = new HashMap();
        classFields.put(clazz, fs);
        //加载属性
        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            fs.put(f.getName(), f);
        }
        //加载属性
        for (Field f : clazz.getFields()) {
            if (!fs.containsKey(f.getName())) {//避免被父类的属性覆盖
                f.setAccessible(true);
                fs.put(f.getName(), f);
            }
        }
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(FieldSelector.class)) {
                selectors.put(clazz, f);
            }
        }
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonElement toJson() {
        try {
            return toJson.toJsonElement(this);
        } catch (Exception ex) {
            throw new RuntimeException("转换为Json失败", ex);
        }
    }

    /**
     * 根据Json创建Beans
     *
     * @param <T>
     * @param clazz
     * @param json
     *
     * @return
     *
     * @throws Exception
     */
    public static <T extends Beans> T create(Class<T> clazz, JsonObject json) throws Exception {
        return fromJson.create(clazz, json);
    }

    public static boolean isBeans(Object o) {
        return isBeans(o.getClass());

    }

    public static boolean isBeans(Class o) {
        return o.isAnnotationPresent(BeansAnnotation.class);
    }

    /**
     * 根据InnerBeansSelector注解,选择可以实例化的子类
     *
     * @param <T>
     * @param clazz
     * @param json
     *
     * @return
     */
    private static <T extends Beans> Class<T> selectClass(Class<T> clazz, JsonObject json) {
        Field inner = selectors.get(clazz);
        if (inner != null) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                //抽象
                String sub = json.get(inner.getName()).getAsString();
                FieldSelector selector = inner.getAnnotation(FieldSelector.class);
                int n = -1;
                String[] values = selector.value();
                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals(sub)) {
                        n = i;
                        break;
                    }
                }
                if (n == -1) {
                    throw new RuntimeException("选择器" + inner.toString() + "中+项:" + sub);
                }
//                System.out.println(clazz.getName() + "中的选择器:" + selector.value()[n] + ",选择类:" + selector.clazz()[n].getName() + ",子节点:" + sub);
                return selectClass(selector.clazz()[n], json);//递归
            }
        }
        //没有选择器,应直接实例化
        return clazz;
    }

    private static class toJson {

        /**
         * 参数必须为数组
         */
        private static JsonArray toJsonArray(Object o) throws Exception {
            JsonArray json = new JsonArray();
            int length = Array.getLength(o);
            if (length == 0) {
                return json;
            }
            for (int i = 0; i < length; i++) {
                JsonElement ele = toJsonElement(Array.get(o, i));
                if (ele != null) {
                    json.add(ele);
                }
            }
            return json;
        }

        /**
         * 元素类型解析
         *
         * @param o
         *
         * @return
         */
        public static JsonElement toJsonElement(Object o) throws Exception {
            if (o == null) {
                return null;
            }
            if (isBeans(o)) {
                return toJsonObject(o);
            }
            if (o.getClass().isArray()) {
                return toJsonArray(o);
            }
            if (o instanceof Set) {
                Set os = (Set) o;
                return toJsonArray(os.toArray());
            }
            if (o instanceof List) {
                List os = (List) o;
                return toJsonArray(os.toArray());
            }
            if (o instanceof Map) {
                return mapToJsonObject((Map<String, Object>) o);
            }
            return toJsonPrimitive(o);
        }

        private static JsonObject mapToJsonObject(Map<String, Object> map) throws Exception {
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, Object> en : map.entrySet()) {
                jo.add(en.getKey(), toJsonElement(en.getValue()));
            }
            return jo;
        }

        private static JsonObject toJsonObject(Object o) throws Exception {
            Map<String, Field> fs = classFields.get(o.getClass());
            JsonObject json = new JsonObject();
            for (Map.Entry<String, Field> en : fs.entrySet()) {
                String name = en.getKey();
                Object f = en.getValue().get(o);
                JsonElement ele = toJsonElement(f);
                if (ele != null) {
                    json.add(name, ele);
                }
            }
            return json;
        }

        private static JsonPrimitive toJsonPrimitive(Object o) {
            if (o instanceof Boolean) {
                return new JsonPrimitive((Boolean) o);
            }
            if (o instanceof Number) {
                return new JsonPrimitive((Number) o);
            }
            if (o instanceof String) {
                return new JsonPrimitive((String) o);
            }
            if (o instanceof Character) {
                return new JsonPrimitive((Character) o);
            }
            throw new RuntimeException("不支持的类型:" + o.getClass().getName());
        }
    }

    private static class fromJson {

        private static Object createPrimitiveObject(Class clazz, Object o) {
            if (o instanceof JsonPrimitive) {
                JsonPrimitive ele = (JsonPrimitive) o;
                if (clazz == int.class || clazz == Integer.class) {
                    return ele.getAsInt();
                } else if (clazz == short.class || clazz == Short.class) {
                    return ele.getAsShort();
                } else if (clazz == byte.class || clazz == Byte.class) {
                    return ele.getAsByte();
                } else if (clazz == long.class || clazz == Long.class) {
                    return ele.getAsLong();
                } else if (clazz == float.class || clazz == Float.class) {
                    return ele.getAsFloat();
                } else if (clazz == double.class || clazz == Double.class) {
                    return ele.getAsDouble();
                } else if (clazz == boolean.class || clazz == Boolean.class) {
                    return ele.getAsBoolean();
                } else if (clazz == char.class || clazz == Character.class || clazz == String.class) {
                    return ele.getAsString();
                } else {
                    throw new RuntimeException("unknown rimitive type:" + clazz.getName());
                }
            } else {
                return o;
            }
        }

        /**
         * 根据Json创建Beans
         *
         * @param <T>
         * @param clazz
         * @param json
         *
         * @return
         *
         * @throws Exception
         */
        public static <T extends Beans> T create(Class<T> clazz, JsonObject json) throws Exception {
            clazz = selectClass(clazz, json);
            T instance;
            try {
                instance = clazz.newInstance();
            } catch (IllegalAccessException | InstantiationException ex) {
                Clann.logWarning("实例化失败:" + clazz.getName());
                throw ex;
            }
            Map<String, Field> fs = classFields.get(clazz);
            if (fs == null) {
                throw new RuntimeException("类" + clazz.getName() + "未注册,无法解析为Beans");
            }
            for (Map.Entry<String, JsonElement> en : json.entrySet()) {
                String key = en.getKey();
                JsonElement value = en.getValue();
                Field f = fs.get(key);
                if (f != null) {
                    Object o = createObject(f, f.getType(), value);
                    f.set(instance, o);
                } else {
                    Clann.logWarning(clazz.getName() + "中没有属性:" + key + ",忽略之");
                }
            }
            return instance;
        }

        /**
         * 创建,clazz不能为Collection、Map的子类
         *
         * @param clazz
         * @param ele
         *
         * @return
         *
         * @throws Exception
         */
        private static Object createObject(Class clazz, JsonElement ele) throws Exception {
            return createObject(null, clazz, ele);
        }

        /**
         * 创建节点
         *
         * @param holder
         * @param clazz
         * @param ele
         *
         * @return
         *
         * @throws Exception
         */
        private static Object createObject(Field holder, Class clazz, JsonElement ele) throws Exception {
            if (ele.isJsonPrimitive()) {//基本元素
                return createPrimitiveObject(clazz, ele);
            } else if (Set.class.isAssignableFrom(clazz)) {//Set
                return createCollection(holder, clazz, ele.getAsJsonArray());
            } else if (List.class.isAssignableFrom(clazz)) {//List
                return createCollection(holder, clazz, ele.getAsJsonArray());
            } else if (Map.class.isAssignableFrom(clazz)) {//Map
                return createMap(holder, clazz, ele.getAsJsonObject());
            } else if (clazz.isArray()) {//数组
                return createArray(clazz.getComponentType(), ele.getAsJsonArray());
            } else if (isBeans(clazz)) {//Beans
                return create(clazz, ele.getAsJsonObject());
            } else {
                throw new RuntimeException("unknown json type:" + ele.getClass().getName());
            }
        }

        /**
         * 根据属性的注解创建集合实例
         *
         * @param <T>
         * @param holder 存储该实力的属性
         * @param clazz 存储元素的类
         * @param arr JsonArray
         *
         * @return
         *
         * @throws Exception
         */
        private static Collection createCollection(Field holder, Class clazz, JsonArray arr) throws Exception {
            Collection co;
            if (Modifier.isAbstract(clazz.getModifiers())) {
                if (Set.class.isAssignableFrom(clazz)) {
                    co = new HashSet();//抽象,默认使用HashSet
                } else if (List.class.isAssignableFrom(clazz)) {
                    co = new LinkedList();//抽象,默认使用LinkedList
                } else {
                    throw new RuntimeException("unknown collection type:" + clazz.getName());
                }
            } else {
                co = (Collection) clazz.newInstance();//实例化
            }
            for (JsonElement en : arr) {
                co.add(createObject(holder.getAnnotation(ComponentType.class).clazz(), en));
            }
            return co;
        }

        /**
         * 根据属性的注解创建Map实例
         *
         * @param holder
         * @param clazz
         * @param arr
         *
         * @return
         *
         * @throws Exception
         */
        private static Map createMap(Field holder, Class clazz, JsonObject arr) throws Exception {
            Map map;
            if (Modifier.isAbstract(clazz.getModifiers())) {
                //抽象,默认使用HashMap
                map = new HashMap();
            } else {
                //实例化
                map = (Map) clazz.newInstance();
            }
            for (Map.Entry<String, JsonElement> en : arr.entrySet()) {
                map.put(en.getKey(), createObject(holder.getAnnotation(ComponentType.class).clazz(), en.getValue()));
            }
            return map;
        }

        /**
         * 创建数组
         *
         * @param <T>
         * @param clazz 数组元素的类型
         * @param arr
         *
         * @return
         *
         * @throws Exception
         */
        private static Object createArray(Class clazz, JsonArray arr) throws Exception {
            int size = arr.size();
            Object o = Array.newInstance(clazz, size);
            for (int i = 0; i < size; i++) {
                Array.set(o, i, createObject(clazz, arr.get(i)));
            }
            return o;
        }

    }

}
