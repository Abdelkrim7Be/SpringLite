package com.bellagnech.springlite.di;

/**
 * Central interface to provide configuration for an application.
 * This is read-only while the application is running, but may be
 * reloaded if the implementation supports this.
 */
public interface ApplicationContext extends BeanFactory {
    
    /**
     * Load or refresh the configuration.
     * This includes bean definitions and properties.
     * 
     * @throws Exception if the configuration cannot be loaded
     */
    void refresh() throws Exception;
}
