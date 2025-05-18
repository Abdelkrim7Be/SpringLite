package com.bellagnech.springlite.di.annotation;

import com.bellagnech.springlite.di.BeanCreationException;
import com.bellagnech.springlite.di.BeanDefinition;
import com.bellagnech.springlite.di.BeanDefinitionRegistry;
import com.bellagnech.springlite.di.annotations.Component;
import com.bellagnech.springlite.di.annotations.Scope;
import com.bellagnech.springlite.di.util.ClasspathScanner;

import java.util.List;

/**
 * Bean definition reader that scans and registers beans based on annotations.
 * Looks for classes with @Component annotation and creates bean definitions from them.
 */
public class AnnotationBeanDefinitionReader {
    
    private final BeanDefinitionRegistry registry;
    
    public AnnotationBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * Scan the given packages for bean candidates.
     * 
     * @param basePackages the packages to scan
     * @throws Exception if an error occurs during scanning
     */
    public void scan(String... basePackages) throws Exception {
        for (String basePackage : basePackages) {
            List<Class<?>> classes = ClasspathScanner.findClassesInPackage(basePackage);
            
            for (Class<?> clazz : classes) {
                if (isComponent(clazz)) {
                    registerBeanDefinition(clazz);
                }
            }
        }
    }
    
    /**
     * Check if a class is annotated with @Component.
     * 
     * @param clazz the class to check
     * @return true if the class is a component
     */
    private boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class);
    }
    
    /**
     * Register a bean definition from the annotated class.
     * 
     * @param clazz the annotated class
     * @throws BeanCreationException if bean registration fails
     */
    private void registerBeanDefinition(Class<?> clazz) throws BeanCreationException {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setClassName(clazz.getName());
        
        // Determine bean ID (name)
        Component componentAnnotation = clazz.getAnnotation(Component.class);
        String beanName = componentAnnotation.value();
        
        // If no explicit name, use the simple class name with lowercase first letter
        if (beanName == null || beanName.isEmpty()) {
            String simpleName = clazz.getSimpleName();
            beanName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        }
        
        beanDefinition.setId(beanName);
        
        // Check for scope annotation
        if (clazz.isAnnotationPresent(Scope.class)) {
            Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
            beanDefinition.setScope(scopeAnnotation.value());
        }
        
        // Register the bean definition
        registry.registerBeanDefinition(beanDefinition);
    }
}
