package com.bellagnech.springlite.di.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility for the framework.
 */
public class Logger {
    
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    private static Level currentLevel = Level.INFO;
    private static boolean enabled = true;
    private final String name;
    
    public Logger(String name) {
        this.name = name;
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }
    
    public static void setLevel(Level level) {
        currentLevel = level;
    }
    
    public static void enable() {
        enabled = true;
    }
    
    public static void disable() {
        enabled = false;
    }
    
    public void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    public void info(String message) {
        log(Level.INFO, message);
    }
    
    public void warn(String message) {
        log(Level.WARN, message);
    }
    
    public void error(String message) {
        log(Level.ERROR, message);
    }
    
    public void error(String message, Throwable t) {
        error(message + ": " + t.getMessage());
        if (currentLevel == Level.DEBUG) {
            t.printStackTrace();
        }
    }
    
    private void log(Level level, String message) {
        if (enabled && level.ordinal() >= currentLevel.ordinal()) {
            System.out.println(getTimestamp() + " [" + level + "] " + name + " - " + message);
        }
    }
    
    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
