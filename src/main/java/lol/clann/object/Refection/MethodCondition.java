/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.Refection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lol.clann.Utils.ReflectionUtils;
import static lol.clann.Utils.ReflectionUtils.getRefClass;
import static lol.clann.Utils.ReflectionUtils.isForge;

/**
 *
 * @author zyp
 */
public class MethodCondition implements Cloneable {

    private String name;
    private String prefix;
    private String suffix;
    private boolean checkForge;
    private boolean forge;
    private Class returnType;
    private List<Class> types;
    private int index = -1;
    private boolean checkAbstract = false;
    private boolean modAbstract;
    private boolean checkFinal = false;
    private boolean modFinal;
    private boolean checkStatic = false;
    private boolean modStatic;

    public MethodCondition withForge(boolean forge) {
        this.checkForge = true;
        this.forge = forge;
        return this;
    }

    public MethodCondition withName(String name) {
        this.name = name;
        return this;
    }

    public MethodCondition withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public MethodCondition withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public MethodCondition withReturnType(Class returnType) {
        this.returnType = returnType;
        return this;
    }

    public MethodCondition withReturnType(String pattern) {
        return withReturnType(getRefClass(pattern));
    }

    public MethodCondition withReturnType(RefClass returnType) {
        this.returnType = returnType.getRealClass();
        return this;
    }

    public MethodCondition withTypes(Object... types) {
        this.types = new ArrayList<Class>();
        for (Object type : types) {
            if (type instanceof Class) {
                this.types.add((Class) type);
            } else if (type instanceof RefClass) {
                this.types.add(((RefClass) type).getRealClass());
            } else if (type instanceof String) {
                this.types.add(getRefClass((String) type).getRealClass());
            } else {
                throw new IllegalArgumentException(type + " is not a Class or RefClass");
            }
        }
        return this;
    }

    public MethodCondition withAbstract(boolean modAbstract) {
        this.checkAbstract = true;
        this.modAbstract = modAbstract;
        return this;
    }

    public MethodCondition withFinal(boolean modFinal) {
        this.checkFinal = true;
        this.modFinal = modFinal;
        return this;
    }

    public MethodCondition withStatic(boolean modStatic) {
        this.checkStatic = true;
        this.modStatic = modStatic;
        return this;
    }

    public MethodCondition withIndex(int index) {
        this.index = index;
        return this;
    }

    RefMethod find(RefClass clazz) {
        return find(clazz.getRealClass());
    }

    RefMethod find(Class clazz) {
        List<Method> methods = new ArrayList<Method>();
        for (Method m : clazz.getMethods()) {
            if (!methods.contains(m)) {
                methods.add(m);
            }
        }
        for (Method m : clazz.getDeclaredMethods()) {
            if (!methods.contains(m)) {
                methods.add(m);
            }
        }

        if (checkForge) {
            if (isForge() != forge) {
                throw new RuntimeException("Forge condition: " + forge);
            }
        }
        if (name != null) {
            Iterator<Method> itr = methods.iterator();
            while (itr.hasNext()) {
                if (!itr.next().getName().equals(name)) {
                    itr.remove();
                }
            }
        }
        if (prefix != null) {
            Iterator<Method> itr = methods.iterator();
            while (itr.hasNext()) {
                if (!itr.next().getName().startsWith(prefix)) {
                    itr.remove();
                }
            }
        }
        if (suffix != null) {
            Iterator<Method> itr = methods.iterator();
            while (itr.hasNext()) {
                if (!itr.next().getName().endsWith(suffix)) {
                    itr.remove();
                }
            }
        }
        if (returnType != null) {
            Iterator<Method> itr = methods.iterator();
            while (itr.hasNext()) {
                if (!itr.next().getReturnType().equals(returnType)) {
                    itr.remove();
                }
            }
        }
        if (checkAbstract) {
            Iterator<Method> itr = methods.iterator();
            while (itr.hasNext()) {
                if (Modifier.isAbstract(itr.next().getModifiers()) != modAbstract) {
                    itr.remove();
                }
            }
        }
        if (checkFinal) {
            Iterator<Method> itr = methods.iterator();
            while (itr.hasNext()) {
                if (Modifier.isFinal(itr.next().getModifiers()) != modFinal) {
                    itr.remove();
                }
            }
        }
        if (checkStatic) {
            Iterator<Method> itr = methods.iterator();
            while (itr.hasNext()) {
                if (Modifier.isStatic(itr.next().getModifiers()) != modStatic) {
                    itr.remove();
                }
            }
        }
        if (types != null) {
            Iterator<Method> itr = methods.iterator();
            itr:
            while (itr.hasNext()) {
                Method method = itr.next();
                Class[] classes = method.getParameterTypes();
                if (classes.length != types.size()) {
                    itr.remove();
                    continue;
                }
                for (int i = 0; i < classes.length; i++) {
                    if (!classes[i].equals(types.get(i))) {
                        itr.remove();
                        continue itr;
                    }
                }
            }
        }
        if (methods.size() == 0) {
            throw new RuntimeException("no such method");
        } else if (methods.size() == 1) {
            return new RefMethod(methods.iterator().next());
        } else if (index < 0) {
            throw new RuntimeException("more than one method found: " + methods);
        } else if (index >= methods.size()) {
            throw new RuntimeException("No more methods: " + methods);
        } else {
            return new RefMethod(methods.get(index));
        }
    }
}
