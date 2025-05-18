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
    public void registerBeanDefinition(BeanDefinition beanDefinition) throws BeanCreationException {
        String beanId = beanDefinition.getId();
        if (beanId == null || beanId.isEmpty()) {
            throw new BeanCreationException(beanId, "Bean ID cannot be null or empty");
        }
        
        beanDefinitionMap.put(beanId, beanDefinition);
    }
    
    @Override
    public BeanDefinition getBeanDefinition(String beanId) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = beanDefinitionMap.get(beanId);
        if (bd == null) {
            throw new NoSuchBeanDefinitionException(beanId);
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
