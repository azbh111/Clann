package lol.clann.Utils;

import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.util.*;
import lol.clann.object.Refection.RefClass;
import lol.clann.object.Refection.RefField;
import lol.clann.object.Refection.RefMethod;

/**
 * @author DPOH-VAR
 * @version 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public class ReflectionUtils {

    /**
     * boolean value, TRUE if server uses forge or MCPC+
     */
    private static boolean forge = false;
    /**
     * class loader, needed for MCPC+
     */
    private static ClassLoader classLoader = Bukkit.getServer().getClass().getClassLoader();
    /**
     * classLoader in class names
     */
    private static HashMap<String, String> replacements = new HashMap<String, String>();

    /**
     * check server version and class names
     */
    static {
        replacements.put("cb", "org.bukkit.craftbukkit");
        replacements.put("nm", "net.minecraft");
        replacements.put("nms", "net.minecraft.server");
        if (Bukkit.getServer() != null) {
            String version = Bukkit.getVersion();
            if (version.contains("MCPC")) {
                forge = true;
            } else if (version.contains("Forge")) {
                forge = true;
            } else if (version.contains("Cauldron")) {
                forge = true;
            } else {
                try {
                    Class.forName("net.minecraft.nbt.NBTBase");
                    forge = true;
                } catch (ClassNotFoundException ignored) {
                }
            }
            String[] pas = Bukkit.getServer().getClass().getName().split("\\.");
            if (pas.length == 5) {
                replacements.put("cb", "org.bukkit.craftbukkit." + pas[3]);
                replacements.put("nms", "net.minecraft.server." + pas[3]);
            }
        }
    }

    /**
     * @return true if server has forge classes
     */
    public static boolean isForge() {
        return forge;
    }

    /**
     * Get class for name. Replace {nms} to net.minecraft.server.V*. Replace
     * {cb} to org.bukkit.craftbukkit.V*. Replace {nm} to net.minecraft
     *
     * @param pattern possible class paths, split by ","
     * @return RefClass object
     * @throws RuntimeException if no class found
     */

    
    public static RefClass getRefClass(String pattern) {
        String[] vars;
        if (pattern.contains(" ") || pattern.contains(",")) {
            vars = pattern.split(" |,");
        } else {
            vars = new String[1];
            vars[0] = pattern;
        }
        for (String name : vars) {
            if (!name.isEmpty()) {
                try {
                    Class clazz = classByName(name);
                    if (clazz == null) {
                        return null;
                    } else {
                        return new RefClass(clazz);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        throw new RuntimeException("no class found: " + pattern);
    }

   
    public static RefClass getRefClass(RefMethod o) {
        return getRefClass(o.getRealMethod().getReturnType());
    }

    
    public static RefClass getRefClass(Field o) {
        return getRefClass(o.getType());
    }

    
    public static RefClass getRefClass(RefField o) {
        return getRefClass(o.getRealField().getType());
    }

    private static HashMap<String, Class> classPatterns = new HashMap<String, Class>() {
        {
            put("null", null);
            put("*", null);
            put("void", void.class);
            put("boolean", boolean.class);
            put("byte", byte.class);
            put("short", short.class);
            put("int", int.class);
            put("long", long.class);
            put("float", float.class);
            put("double", double.class);
            put("boolean[]", boolean[].class);
            put("byte[]", byte[].class);
            put("char[]", char[].class);
            put("short[]", short[].class);
            put("int[]", int[].class);
            put("long[]", long[].class);
            put("float[]", float[].class);
            put("double[]", double[].class);
        }
    };

    private static Class classByName(String pattern) throws ClassNotFoundException {
        if (classPatterns.containsKey(pattern)) {
            return classPatterns.get(pattern);
        }
        for (Map.Entry<String, String> e : replacements.entrySet()) {
            pattern = pattern.replace("{" + e.getKey() + "}", e.getValue());
        }
        return classLoader.loadClass(pattern);
    }

    /**
     * get RefClass object by real class
     *
     * @param clazz class
     * @param <T> type of inner class
     * @return RefClass based on passed class
     */
    public static <T> RefClass<T> getRefClass(Class<T> clazz) {
        return new RefClass<T>(clazz);
    }



}
