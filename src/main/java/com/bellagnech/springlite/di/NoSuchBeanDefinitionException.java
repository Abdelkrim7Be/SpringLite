package com.bellagnech.springlite.di;

/**
 * Exception thrown when a bean definition cannot be found.
 */
public class NoSuchBeanDefinitionException extends Exception {
    
    private final String beanId;
    
    public NoSuchBeanDefinitionException(String beanId) {
        super("No bean definition found for bean ID: " + beanId);
        this.beanId = beanId;
    }
    
    public String getBeanId() {
        return beanId;
    }
}
