package org.marceloleite.projetoanna.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls which messages should be registered on log.
 */
public class Log {

    /**
     * The list of classes which can register its messages on log.
     */
    private static List<String> logTagList = new ArrayList<>();

    /**
     * Object constructor.
     */
    private Log() {
    }

    /**
     * Adds a log tag to the class list, enabling its messages to be printed on log.
     *
     * @param tag The tag enabled to show its messages on log.
     */
    public static void addClassToLog(String tag) {
        if (!isLogTagEnabled(tag)) {
            logTagList.add(tag);
        }
    }

    /**
     * Removes a log tag from the list, prohibiting its messages to be printed on log.
     *
     * @param tag The tag which will be restrained on log.
     */
    @SuppressWarnings("unused")
    public static void removeClassFromLog(String tag) {
        if (isLogTagEnabled(tag)) {
            logTagList.remove(tag);
        }
    }

    /**
     * Checks if a log tag is enabled to print its messages.
     *
     * @param logTag The log tag to be checked.
     * @return True if log tag is enabled to print its messages on log. False otherwise.
     */
    private static boolean isLogTagEnabled(String logTag) {
        int classPosition = logTagList.indexOf(logTag);
        return (classPosition != -1);
    }

    /**
     * Prints a warning message on log if its log tag is enabled.
     *
     * @param tag     The log tag to be used on this message.
     * @param message The message to be registered.
     */
    public static void w(String tag, String message) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.w(tag, message);
        }
    }

    /**
     * Prints a warning message on log if its log tag is enabled.
     *
     * @param tag       The log tag to be used on this message.
     * @param message   The message to be registered.
     * @param throwable The throwable to be printed on log message.
     */
    @SuppressWarnings("unused")
    public static void w(String tag, String message, Throwable throwable) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.w(tag, message, throwable);
        }
    }

    /**
     * Prints an error message on log if its log tag is enabled.
     *
     * @param tag     The log tag to be used on this message.
     * @param message The message to be registered.
     */
    public static void e(String tag, String message) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.e(tag, message);
        }
    }

    /**
     * Prints an error message on log if its log tag is enabled.
     *
     * @param tag       The log tag to be used on this message.
     * @param message   The message to be registered.
     * @param throwable The throwable to be printed on log message.
     */
    public static void e(String tag, String message, Throwable throwable) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.e(tag, message, throwable);
        }
    }

    /**
     * Prints an information message on log if its log tag is enabled.
     *
     * @param tag     The log tag to be used on this message.
     * @param message The message to be registered.
     */
    public static void i(String tag, String message) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.i(tag, message);
        }
    }

    /**
     * Prints an information message on log if its log tag is enabled.
     *
     * @param tag       The log tag to be used on this message.
     * @param message   The message to be registered.
     * @param throwable The throwable to be printed on log message.
     */
    @SuppressWarnings("unused")
    public static void i(String tag, String message, Throwable throwable) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.i(tag, message, throwable);
        }
    }

    /**
     * Prints a debug message on log if its log tag is enabled.
     *
     * @param tag     The log tag to be used on this message.
     * @param message The message to be registered.
     */
    public static void d(String tag, String message) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.d(tag, message);
        }
    }

    /**
     * Prints a debug message on log if its log tag is enabled.
     *
     * @param tag       The log tag to be used on this message.
     * @param message   The message to be registered.
     * @param throwable The throwable to be printed on log message.
     */
    public static void d(String tag, String message, Throwable throwable) {
        if (isLogTagEnabled(tag)) {
            android.util.Log.d(tag, message, throwable);
        }
    }
}
