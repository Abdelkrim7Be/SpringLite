package com.bellagnech.springlite.di.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility class for scanning the classpath to find classes in a specific package.
 */
public class ClasspathScanner {
    
    /**
     * Find all classes in a package.
     * 
     * @param packageName the package to scan
     * @return list of classes found in the package
     * @throws Exception if an error occurs during scanning
     */
    public static List<Class<?>> findClassesInPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClasspathScanner.class.getClassLoader();
        }
        
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        
        return classes;
    }
    
    /**
     * Recursive method to find classes in directories.
     * 
     * @param directory the directory to scan
     * @param packageName the package name for classes found
     * @return list of classes found in the directory
     * @throws ClassNotFoundException if a class cannot be loaded
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
        
        return classes;
    }
}
