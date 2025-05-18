package com.bellagnech.springlite.di;

import java.util.Set;

/**
 * Exception thrown when a circular dependency is detected.
 */
public class CircularDependencyException extends BeanCreationException {
    
    private final Set<String> beanChain;
    
    public CircularDependencyException(String beanId, Set<String> beanChain) {
        super(beanId, "Circular reference detected: " + formatBeanChain(beanId, beanChain));
        this.beanChain = beanChain;
    }
    
    public Set<String> getBeanChain() {
        return beanChain;
    }
    
    private static String formatBeanChain(String currentBean, Set<String> beanChain) {
        StringBuilder sb = new StringBuilder();
        for (String bean : beanChain) {
            if (sb.length() > 0) {
                sb.append(" -> ");
            }
            sb.append(bean);
        }
        sb.append(" -> ").append(currentBean);
        return sb.toString();
    }
}
