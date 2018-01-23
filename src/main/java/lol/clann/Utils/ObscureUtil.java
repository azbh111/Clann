/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import lol.clann.Clann;
import lol.clann.api.ClassApi;
import lol.clann.object.ObscureClass;
import org.bukkit.Bukkit;
import org.w3c.dom.*;

//未完成
//由于多态方法的混淆名不同,暂时不能准确获得混淆名,故暂不适用
public class ObscureUtil {

    private static Map<String, ObscureClass> Obscure = new HashMap();

    public static void init() {
    }

    static {
        try {
            InputStream is = Clann.class.getResourceAsStream("/obscure/ASMShooterData-1.7.10.xml");
            NodeList packages = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement().getChildNodes();
            is.close();
            for (int i = 0; i < packages.getLength(); i++) {//遍历包
                Node pac = packages.item(i);
                if (!pac.getNodeName().equals("Package")) {
                    continue;
                }
                NodeList clazzs = pac.getChildNodes();
                for (int j = 0; j < clazzs.getLength(); j++) {//遍历类
                    Node clazz = clazzs.item(j);
                    if (!clazz.getNodeName().equals("Class")) {
                        continue;
                    }
                    Obscure.put(clazz.getAttributes().getNamedItem("FullName").getNodeValue().replaceAll("/", "."), new ObscureClass(clazz));
                }
            }
            Clann.log("混淆规则统计:");
            Clann.log("  " + Obscure.size() + "个类");
            int fields = 0;
            int methods = 0;
            for (ObscureClass oc : Obscure.values()) {
                fields += oc.getFieldSize();
                methods += oc.getMethodSize();
            }
            Clann.log("  建立" + fields + "条属性混淆规则");
            Clann.log("  建立" + methods + "条方法混淆规则");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }
    }

    public static String getMethodSeargeName(Object clazz, Object returnClass, String name, Object[] parms) {
        Class[] cs = new Class[parms.length];
        for (int i = 0; i < parms.length; i++) {
            cs[i] = ClassApi.warpClass(parms[i]);
        }
        String re = me.getMethodSeargeName(ClassApi.warpClass(clazz), ClassApi.warpClass(returnClass), name, cs);
        return re != null ? re : name;
    }

    public static String getFieldSeargeName(Object clazz, String name) {
        String re = me.getFieldSeargeName(ClassApi.warpClass(clazz), name);

        return re != null ? re : name;
    }

    private static class me {

        private static String getFieldSeargeName(Class clazz, String name) {
            ObscureClass oc = Obscure.get(clazz.getName());
            if (oc != null) {
                String s = oc.getFieldSeargeName(name);
                if (s != null) {
                    return s;
                } else {
                    return getFieldSeargeNameFromParentClass(clazz, name);
                }
            } else {
                return getFieldSeargeNameFromParentClass(clazz, name);
            }
        }

        private static String getFieldSeargeNameFromParentClass(Class clazz, String name) {
            List<Class> superClass = getParentClass(clazz);
            if (superClass == null) {
                return null;
            } else {
                String re;
                for (Class c : superClass) {
                    re = getFieldSeargeName(c, name);
                    if (re != null) {
                        return re;
                    }
                }
                return null;
            }
        }

        private static List<Class> getParentClass(Class clazz) {
            try {
                if (clazz.equals(Object.class)) {
                    return null;
                }
                List<Class> superClass = new ArrayList();
                if (clazz.getInterfaces() != null && clazz.getInterfaces().length > 0) {
                    superClass.addAll(Arrays.asList(clazz.getInterfaces()));
                }
                if (clazz.getSuperclass() != null) {
                    superClass.add(clazz.getSuperclass());
                }
                
                return superClass.isEmpty() ? null : superClass;
            } catch (Throwable e) {
                return null;
            }
        }

        private static String getMethodSeargeName(Class clazz, Class returnClass, String name, Class[] parms) {
            ObscureClass oc = Obscure.get(clazz.getName());
            if (oc != null) {
                String s = oc.getMethodSeargeName(name, getDesc(returnClass, parms));
                if (s != null) {
                    return s;
                } else {
                    return getMethodSeargeNameFromParentClass(clazz, returnClass, name, parms);
                }
            } else {

                return getMethodSeargeNameFromParentClass(clazz, returnClass, name, parms);
            }
        }

        private static String getMethodSeargeNameFromParentClass(Class clazz, Class returnClass, String name, Class[] parms) {
            List<Class> superClass = getParentClass(clazz);
            if (superClass == null) {
                return null;
            } else {
                String re;
                for (Class c : superClass) {
                    re = getMethodSeargeName(c, returnClass, name, parms);
                    if (re != null) {
                        return re;
                    }
                }
                return null;
            }
        }

        private static String getDesc(Class re, Class[] parms) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (Class c : parms) {
                sb.append(ClassApi.getNameForBytecode(c));
            }
            sb.append(")");
            sb.append(ClassApi.getNameForBytecode(re));
            return sb.toString();
        }
    }

}
