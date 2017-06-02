package org.marceloleite.projetoanna.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelo on 02/06/17.
 */

public class Log {

    private static List<Class> classList = new ArrayList<>();

    private Log() {
    }

    public static void addClassToLog(Class clazz) {
        if (!isClassOnList(clazz)) {
            classList.add(clazz);
        }
    }

    public static void removeClassFromLog(Class clazz) {
        if (isClassOnList(clazz)) {
            classList.remove(clazz);
        }
    }

    private static boolean isClassOnList(Class clazz) {
        int classPosition = classList.indexOf(clazz);
        return (classPosition != -1);
    }

    public static void w(Class clazz, String tag, String message) {
        if (isClassOnList(clazz)) {
            android.util.Log.w(tag, message);
        }
    }

    public static void w(Class clazz, String tag, String message, Throwable throwable) {
        if (isClassOnList(clazz)) {
            android.util.Log.w(tag, message, throwable);
        }
    }

    public static void e(Class clazz, String tag, String message) {
        if (isClassOnList(clazz)) {
            android.util.Log.e(tag, message);
        }
    }

    public static void e(Class clazz, String tag, String message, Throwable throwable) {
        if (isClassOnList(clazz)) {
            android.util.Log.e(tag, message, throwable);
        }
    }

    public static void i(Class clazz, String tag, String message) {
        if (isClassOnList(clazz)) {
            android.util.Log.i(tag, message);
        }
    }

    public static void i(Class clazz, String tag, String message, Throwable throwable) {
        if (isClassOnList(clazz)) {
            android.util.Log.i(tag, message, throwable);
        }
    }

    public static void d(Class clazz, String tag, String message) {
        if (isClassOnList(clazz)) {
            android.util.Log.d(tag, message);
        }
    }

    public static void d(Class clazz, String tag, String message, Throwable throwable) {
        if (isClassOnList(clazz)) {
            android.util.Log.d(tag, message, throwable);
        }
    }
}
