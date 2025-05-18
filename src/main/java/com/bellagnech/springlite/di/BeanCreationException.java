package com.bellagnech.springlite.di;

/**
 * Exception thrown when a bean cannot be created.
 */
public class BeanCreationException extends Exception {
    
    private final String beanId;
    
    public BeanCreationException(String beanId, String message) {
        super("Error creating bean '" + beanId + "': " + message);
        this.beanId = beanId;
    }
    
    public BeanCreationException(String beanId, String message, Throwable cause) {
        super("Error creating bean '" + beanId + "': " + message, cause);
        this.beanId = beanId;
    }
    
    public String getBeanId() {
        return beanId;
    }
}
