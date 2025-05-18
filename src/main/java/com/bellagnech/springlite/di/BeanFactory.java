package com.bellagnech.springlite.di;

/**
 * Interface for accessing beans managed by a container.
 * The basic client view of a bean container.
 */
public interface BeanFactory {
    
    /**
     * Return an instance of the bean registered with the given id.
     * 
     * @param id the bean identifier
     * @return the bean instance
     * @throws Exception if the bean could not be created or no bean is found
     */
    Object getBean(String id) throws Exception;
}
