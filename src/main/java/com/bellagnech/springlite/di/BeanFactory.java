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
     * @throws NoSuchBeanDefinitionException if no bean definition is found
     * @throws BeanCreationException if the bean could not be created
     */
    Object getBean(String id) throws NoSuchBeanDefinitionException, BeanCreationException;
    
    /**
     * Return an instance of the bean registered with the given id, cast to the specified type.
     * 
     * @param <T> the bean type
     * @param id the bean identifier
     * @param requiredType the required type
     * @return the bean instance
     * @throws NoSuchBeanDefinitionException if no bean definition is found
     * @throws BeanCreationException if the bean could not be created or is not of the required type
     */
    <T> T getBean(String id, Class<T> requiredType) throws NoSuchBeanDefinitionException, BeanCreationException;
    
    /**
     * Check if a bean with the given id exists.
     * 
     * @param id the bean identifier
     * @return true if a bean with the given id exists
     */
    boolean containsBean(String id);
}
