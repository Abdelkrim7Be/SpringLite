package com.bellagnech.springlite.di;

import java.util.Map;

/**
 * Interface for registries that hold bean definitions.
 * Used by bean definition readers to register beans.
 */
public interface BeanDefinitionRegistry {
    
    /**
     * Register a new bean definition with this registry.
     * 
     * @param beanDefinition the bean definition to register
     */
    void registerBeanDefinition(BeanDefinition beanDefinition);
    
    /**
     * Return the BeanDefinition for the given bean id.
     * 
     * @param beanId the bean id to look up
     * @return the BeanDefinition for the given name (never null)
     * @throws Exception if no such bean definition exists
     */
    BeanDefinition getBeanDefinition(String beanId) throws Exception;
    
    /**
     * Check if this registry contains a bean definition with the given id.
     * 
     * @param beanId the id of the bean to look for
     * @return true if this registry contains a bean definition with the given id
     */
    boolean containsBeanDefinition(String beanId);
    
    /**
     * Return the names of all beans defined in this registry.
     * 
     * @return the names of all beans defined in this registry,
     *         or an empty array if none defined
     */
    String[] getBeanDefinitionNames();
    
    /**
     * Return all bean definitions as a map from bean id to definition.
     * 
     * @return map of all registered bean definitions
     */
    Map<String, BeanDefinition> getBeanDefinitions();
}
