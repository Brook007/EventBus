package com.brook.app.android.supportlibrary.eventbus.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther Brook
 * @time 2017/5/26 22:19
 */
public class Util {

    private static Map<String, String> nameMap;
    private static Map<String, Object> defaultValue;

    static {
        nameMap = new HashMap<>();
        defaultValue = new HashMap<>();

        nameMap.put(boolean.class.getName(), Boolean.class.getName());
        nameMap.put(char.class.getName(), Character.class.getName());
        nameMap.put(byte.class.getName(), Byte.class.getName());
        nameMap.put(short.class.getName(), Short.class.getName());
        nameMap.put(int.class.getName(), Integer.class.getName());
        nameMap.put(long.class.getName(), Long.class.getName());
        nameMap.put(float.class.getName(), Float.class.getName());
        nameMap.put(double.class.getName(), Double.class.getName());


        defaultValue.put(boolean.class.getName(), false);
        defaultValue.put(char.class.getName(), '\u0000');
        defaultValue.put(byte.class.getName(), 0);
        defaultValue.put(short.class.getName(), 0);
        defaultValue.put(int.class.getName(), 0);
        defaultValue.put(long.class.getName(), 0L);
        defaultValue.put(float.class.getName(), 0.0f);
        defaultValue.put(double.class.getName(), 0.0d);
    }

    public static Set<String> toList(Class[] obj) {
        Set<String> list = new HashSet<>();
        for (Class clazz : obj) {
            list.add(clazz.getName());
        }
        return list;
    }

    private static Class[] toArray(Object[] objects) {
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (classes[i] != null) {
                classes[i] = objects[i].getClass();
            } else {
                classes[i] = null;
            }
        }
        return classes;
    }

    public static boolean compare(Object[] first, Object[] second) {
        return compare(toArray(first), second);
    }

    public static boolean compareTo(Object[] first, Object[] second) {
        if (first.length != second.length) {
            return false;
        } else {
            for (int i = 0; i < first.length; i++) {
                if (first[i] != null) {
                    if (second[i] != null) {
                        if (!first[i].equals(second[i])) {
                            return false;
                        }
                    }
                }
            }
        }
        return compare(toArray(first), second);
    }

    public static boolean compare(Class[] first, Object[] second) {
        if (first.length != second.length) {
            return false;
        } else {
            for (int i = 0; i < first.length; i++) {
                if (first[i] == null) {
                    continue;
                }

                String firstName = first[i].getName();
                if (second[i] == null) {
                    second[i] = defaultValue.get(firstName);
                    continue;
                }
                firstName = getName(firstName);
                String secondName = getName(second[i].getClass());
                if (!firstName.equals(secondName)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static Object[] handleArgument(Object[] second) {
        if (second.length == 0) {
            return new ArrayList<>().toArray(new Object[]{});
        }
        return second;
    }

    public static String getName(Class clazz) {
        return getName(clazz.getName());
    }

    public static String getName(String name) {
        String value = nameMap.get(name);
        if (value == null) {
            return name;
        } else {
            return value;
        }
    }

    public static String getWhoCallMe() {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 2; i < stack.length; i++) {
            StackTraceElement stackTraceElement = stack[i];
            String className = stackTraceElement.getClassName();
            if (!className.startsWith("com.brook.app.android.superlibrary.eventbus")) {
                String[] split = className.split("\\$");
                if (split.length > 0) {
                    return split[0];
                }
            }

        }
        return null;
    }

    private static ExecutorService executorservice;

    // 默认的线程池大小
    private static int DEFAULT_SIZE = 10;

    /**
     * 创建一个默认的线程池
     *
     * @return
     */
    public static ExecutorService getThreadPool() {
        if (executorservice == null) {
            synchronized (Util.class) {
                if (executorservice == null) {
                    executorservice = Executors.newFixedThreadPool(DEFAULT_SIZE);
                }
            }
        }
        return executorservice;
    }
}
