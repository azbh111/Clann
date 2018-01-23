/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.Refection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lol.clann.Utils.ReflectionUtils;
import static lol.clann.Utils.ReflectionUtils.getRefClass;

/**
 * RefClass - utility to simplify work with reflections.
 *
 * @param <T> type of inner class
 */
public class RefClass<T> {

    private final Class<T> clazz;

    /**
     * get passed class
     *
     * @return class
     */
    public Class<T> getRealClass() {
        return clazz;
    }

    public RefClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * see {@link Class#isInstance(Object)}
     *
     * @param object the object to check
     * @return true if object is an instance of this class
     */
    public boolean isInstance(Object object) {
        return clazz.isInstance(object);
    }

    /**
     * get existing method by name and types
     *
     * @param name name
     * @param types method parameters. can be Class or RefClass
     * @return RefMethod object
     * @throws RuntimeException if method not found
     */
    public RefMethod getMethod(String name, Object... types) {
        try {
            Class[] classes = new Class[types.length];
            int i = 0;
            for (Object e : types) {
                if (e instanceof Class) {
                    classes[i++] = (Class) e;
                } else if (e instanceof RefClass) {
                    classes[i++] = ((RefClass) e).getRealClass();
                } else if (e instanceof String) {
                    classes[i++] = ReflectionUtils.getRefClass((String) e).getRealClass();
                } else {
                    classes[i++] = e.getClass();
                }
            }
            try {
                return new RefMethod(clazz.getMethod(name, classes));
            } catch (NoSuchMethodException ignored) {
                return new RefMethod(clazz.getDeclaredMethod(name, classes));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get existing constructor by types
     *
     * @param types parameters. can be Class, RefClass or String
     * @return RefMethod object
     * @throws RuntimeException if constructor not found
     */
    public RefConstructor<T> getConstructor(Object[] types) {
        try {
            Class[] classes = new Class[types.length];
            int i = 0;
            for (Object e : types) {
                if (e instanceof Class) {
                    classes[i++] = (Class) e;
                } else if (e instanceof RefClass) {
                    classes[i++] = ((RefClass) e).getRealClass();
                } else if (e instanceof String) {
                    classes[i++] = getRefClass((String) e).getRealClass();
                } else {
                    throw new IllegalArgumentException(e + " is not a Class or RefClass");
                }
            }
            try {
                return new RefConstructor<T>(clazz.getConstructor(classes));
            } catch (NoSuchMethodException ignored) {
                return new RefConstructor<T>(clazz.getDeclaredConstructor(classes));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * find method by type parameters
     *
     * @param types parameters. can be Class or RefClass
     * @return RefMethod object
     * @throws RuntimeException if method not found
     */
    public RefMethod findMethodByParams(Object... types) {
        Class[] classes = new Class[types.length];
        int t = 0;
        for (Object e : types) {
            if (e instanceof Class) {
                classes[t++] = (Class) e;
            } else if (e instanceof RefClass) {
                classes[t++] = ((RefClass) e).getRealClass();
            } else if (e instanceof String) {
                classes[t++] = getRefClass((String) e).getRealClass();
            } else {
                throw new IllegalArgumentException(e + " not a Class or RefClass");
            }
        }
        List<Method> methods = new ArrayList<Method>();
        Collections.addAll(methods, clazz.getMethods());
        Collections.addAll(methods, clazz.getDeclaredMethods());
        findMethod:
        for (Method m : methods) {
            Class<?>[] methodTypes = m.getParameterTypes();
            if (methodTypes.length != classes.length) {
                continue;
            }
            for (int i = 0; i < classes.length; i++) {
                if (!classes[i].equals(methodTypes[i])) {
                    continue findMethod;
                }
            }
            return new RefMethod(m);
        }
        throw new RuntimeException("no such method");
    }

    /**
     * find method by conditions
     *
     * @param condition conditions to method
     * @return RefMethod object
     * @throws RuntimeException if method not found
     */
    public RefMethod findMethod(MethodCondition... condition) {
        for (MethodCondition c : condition) {
            try {
                if (c == null) {
                    return null;
                }
                return c.find(this);
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("no such method");
    }

    /**
     * find method by name
     *
     * @param pattern possible names of method, split by ","
     * @return RefMethod object
     * @throws RuntimeException if method not found
     */
    public RefMethod findMethodByName(String pattern) {
        String[] vars;
        if (pattern.contains(" ") || pattern.contains(",")) {
            vars = pattern.split(" |,");
        } else {
            vars = new String[1];
            vars[0] = pattern;
        }
        List<Method> methods = new ArrayList<Method>();
        Collections.addAll(methods, clazz.getMethods());
        Collections.addAll(methods, clazz.getDeclaredMethods());
        for (Method m : methods) {
            for (String name : vars) {
                if (m.getName().equals(name)) {
                    return new RefMethod(m);
                }
            }
        }
        throw new RuntimeException("no such method:" + pattern);
    }

    /**
     * find method by return value
     *
     * @param types type of returned value
     * @throws RuntimeException if method not found
     * @return RefMethod
     */
    public <Z> RefMethod<Z> findMethodByReturnType(RefClass<Z>... types) {
        Class<Z>[] classes = new Class[types.length];
        for (int i = 0; i < types.length; i++) {
            classes[i] = types[i].clazz;
        }
        return findMethodByReturnType(classes);
    }

    /**
     * find method by return value
     *
     * @param patterns type of returned value, see {@link #getRefClass(String)}
     * @throws RuntimeException if method not found
     * @return RefMethod
     */
    public RefMethod findMethodByReturnType(String... patterns) {
        for (String pattern : patterns) {
            try {
                return findMethodByReturnType(getRefClass(pattern));
            } catch (RuntimeException ignored) {
            }
        }
        throw new RuntimeException("no such method");
    }

    /**
     * find method by return value
     *
     * @param types type of returned value
     * @return RefMethod
     * @throws RuntimeException if method not found
     */
    @SuppressWarnings("unchecked")
    public <Z> RefMethod<Z> findMethodByReturnType(Class<Z>... types) {
        for (Class<Z> type : types) {
            if (type == null) {
                type = (Class<Z>) void.class;
            }
            List<Method> methods = new ArrayList<Method>();
            Collections.addAll(methods, clazz.getMethods());
            Collections.addAll(methods, clazz.getDeclaredMethods());
            for (Method m : methods) {
                if (type.equals(m.getReturnType())) {
                    return new RefMethod(m);
                }
            }
        }
        throw new RuntimeException("no such method");

    }

    /**
     * find constructor by number of arguments
     *
     * @param number number of arguments
     * @return RefConstructor
     * @throws RuntimeException if constructor not found
     */
    @SuppressWarnings("unchecked")
    public RefConstructor<T> findConstructor(int number) {
        List<Constructor> constructors = new ArrayList<Constructor>();
        Collections.addAll(constructors, clazz.getConstructors());
        Collections.addAll(constructors, clazz.getDeclaredConstructors());
        for (Constructor m : constructors) {
            if (m.getParameterTypes().length == number) {
                return new RefConstructor(m);
            }
        }
        throw new RuntimeException("no such constructor");
    }

    /**
     * get field by name
     *
     * @param name field name
     * @return RefField
     * @throws RuntimeException if field not found
     */
    public RefField getField(String name) {
        try {
            try {
                return new RefField(clazz.getField(name));
            } catch (NoSuchFieldException ignored) {
                return new RefField(clazz.getDeclaredField(name));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * find field by type
     *
     * @param type field type
     * @return RefField
     * @throws RuntimeException if field not found
     */
    public <P> RefField<P> findField(RefClass<P> type) {
        return findField(type.clazz);
    }

    /**
     * find field by type
     *
     * @param pattern field type, see {@link #getRefClass(String)}
     * @return RefField
     * @throws RuntimeException if field not found
     */
    public RefField findField(String pattern) {
        return findField(getRefClass(pattern));
    }

    /**
     * find field by type
     *
     * @param type field type
     * @return RefField
     * @throws RuntimeException if field not found
     */
    @SuppressWarnings("unchecked")
    public <P> RefField<P> findField(Class<P> type) {
        if (type == null) {
            type = (Class<P>) void.class;
        }
        List<Field> fields = new ArrayList<Field>();
        Collections.addAll(fields, clazz.getFields());
        Collections.addAll(fields, clazz.getDeclaredFields());
        for (Field f : fields) {
            if (type.equals(f.getType())) {
                return new RefField(f);
            }
        }
        throw new RuntimeException("no such field");
    }

}
