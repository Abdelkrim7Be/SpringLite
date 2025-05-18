package com.bellagnech.springlite.di;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the BeanDefinitionRegistry interface.
 * Stores bean definitions in a map, allowing bean definition registration and lookup.
 */
public class DefaultBeanDefinitionRegistry implements BeanDefinitionRegistry {
    
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    
    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        String beanId = beanDefinition.getId();
        if (beanId == null || beanId.isEmpty()) {
            throw new IllegalArgumentException("Bean ID cannot be null or empty");
        }
        
        beanDefinitionMap.put(beanId, beanDefinition);
    }
    
    @Override
    public BeanDefinition getBeanDefinition(String beanId) throws Exception {
        BeanDefinition bd = beanDefinitionMap.get(beanId);
        if (bd == null) {
            throw new Exception("No bean definition found for bean ID: " + beanId);
        }
        return bd;
    }
    
    @Override
    public boolean containsBeanDefinition(String beanId) {
        return beanDefinitionMap.containsKey(beanId);
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }
    
    @Override
    public Map<String, BeanDefinition> getBeanDefinitions() {
        return Collections.unmodifiableMap(beanDefinitionMap);
    }
}
