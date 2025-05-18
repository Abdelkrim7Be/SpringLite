package com.bellagnech.springlite.di;

import com.bellagnech.springlite.di.annotation.AnnotationBeanDefinitionReader;
import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Qualifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ApplicationContext implementation that loads beans based on annotations.
 * Scans packages for classes with @Component annotation and
 * handles dependency injection based on @Autowired annotation.
 */
public class AnnotationApplicationContext implements ApplicationContext, BeanDefinitionRegistry {
    
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Map<String, Object> singletonObjects = new HashMap<>();
    private final AnnotationBeanDefinitionReader beanDefinitionReader;
    private final String[] basePackages;
    private final Set<String> currentlyCreatingBeans = new HashSet<>(); // For circular dependency detection
    private final Map<Class<?>, String> typeToBeanNameMap = new HashMap<>(); // For type-based autowiring
    
    /**
     * Create a new AnnotationApplicationContext with the given base packages to scan.
     * 
     * @param basePackages the packages to scan for annotated beans
     */
    public AnnotationApplicationContext(String... basePackages) throws Exception {
        this.basePackages = basePackages;
        this.beanDefinitionReader = new AnnotationBeanDefinitionReader(this);
        refresh();
    }
    
    @Override
    public void refresh() throws Exception {
        // Clear the singleton cache
        this.singletonObjects.clear();
        this.typeToBeanNameMap.clear();
        
        // Scan packages for bean definitions
        if (basePackages != null) {
            beanDefinitionReader.scan(basePackages);
        }
        
        // Build a map of types to bean names for autowiring by type
        buildTypeToBeanNameMap();
        
        // Instantiate all singleton beans
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                getBean(beanName);
            }
        }
    }
    
    /**
     * Build a map of types to bean names for use in autowiring by type.
     * If multiple beans of same type exist, this will keep the last one.
     */
    private void buildTypeToBeanNameMap() throws ClassNotFoundException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            Class<?> beanClass = Class.forName(beanDefinition.getClassName());
            
            // Map the class itself
            typeToBeanNameMap.put(beanClass, beanName);
            
            // Map interfaces implemented by this class
            for (Class<?> interfaceClass : beanClass.getInterfaces()) {
                typeToBeanNameMap.put(interfaceClass, beanName);
            }
        }
    }
    
    @Override
    public Object getBean(String id) throws Exception {
        BeanDefinition beanDefinition = getBeanDefinition(id);
        
        // If bean is a prototype, always create a new instance
        if ("prototype".equals(beanDefinition.getScope())) {
            return createBean(beanDefinition);
        }
        
        // For singleton beans, check if already instantiated
        Object singleton = this.singletonObjects.get(id);
        if (singleton == null) {
            singleton = createBean(beanDefinition);
            this.singletonObjects.put(id, singleton);
        }
        
        return singleton;
    }
    
    /**
     * Create a new bean instance from the given bean definition.
     * 
     * @param beanDefinition the bean definition
     * @return the bean instance
     */
    protected Object createBean(BeanDefinition beanDefinition) throws Exception {
        String beanId = beanDefinition.getId();
        
        // Check for circular dependencies
        if (currentlyCreatingBeans.contains(beanId)) {
            throw new Exception("Circular reference detected for bean: " + beanId);
        }
        
        try {
            // Mark this bean as currently being created
            currentlyCreatingBeans.add(beanId);
            
            // Load the bean class
            Class<?> beanClass = Class.forName(beanDefinition.getClassName());
            
            // Create a new instance (check for autowired constructors)
            Object beanInstance = instantiateBean(beanClass);
            
            // Inject dependencies into fields and setters
            injectFieldDependencies(beanInstance, beanClass);
            injectSetterDependencies(beanInstance, beanClass);
            
            return beanInstance;
        } finally {
            // Remove from currently creating beans
            currentlyCreatingBeans.remove(beanId);
        }
    }
    
    /**
     * Instantiate a bean, preferring constructors annotated with @Autowired.
     * 
     * @param beanClass the bean class
     * @return the bean instance
     */
    private Object instantiateBean(Class<?> beanClass) throws Exception {
        // Look for constructors annotated with @Autowired
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        Constructor<?> autowiredConstructor = null;
        
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                if (autowiredConstructor != null) {
                    throw new Exception("Multiple constructors annotated with @Autowired in " + beanClass.getName());
                }
                autowiredConstructor = constructor;
            }
        }
        
        // If we found an autowired constructor, use it with dependencies
        if (autowiredConstructor != null) {
            return instantiateWithAutowiredConstructor(autowiredConstructor);
        }
        
        // Otherwise, try to use the default constructor
        try {
            Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new Exception("No default constructor found for " + beanClass.getName() + 
                               ". Either add a default constructor or annotate a constructor with @Autowired");
        }
    }
    
    /**
     * Instantiate a bean using a constructor annotated with @Autowired.
     * 
     * @param constructor the autowired constructor
     * @return the bean instance
     */
    private Object instantiateWithAutowiredConstructor(Constructor<?> constructor) throws Exception {
        constructor.setAccessible(true);
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            String qualifierValue = null;
            
            // Check for @Qualifier
            if (parameter.isAnnotationPresent(Qualifier.class)) {
                qualifierValue = parameter.getAnnotation(Qualifier.class).value();
            }
            
            // If there's a qualifier, use it to find the bean
            if (qualifierValue != null && !qualifierValue.isEmpty()) {
                arguments[i] = getBean(qualifierValue);
            } else {
                // Otherwise, try to find by type
                arguments[i] = findBeanByType(parameterType);
            }
        }
        
        return constructor.newInstance(arguments);
    }
    
    /**
     * Inject dependencies into fields annotated with @Autowired.
     * 
     * @param beanInstance the bean instance
     * @param beanClass the bean class
     */
    private void injectFieldDependencies(Object beanInstance, Class<?> beanClass) throws Exception {
        Class<?> currentClass = beanClass;
        
        // Traverse the class hierarchy to find all fields
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    field.setAccessible(true);
                    
                    // Check for @Qualifier
                    String qualifierValue = null;
                    if (field.isAnnotationPresent(Qualifier.class)) {
                        qualifierValue = field.getAnnotation(Qualifier.class).value();
                    }
                    
                    Object dependency;
                    // If there's a qualifier, use it to find the bean
                    if (qualifierValue != null && !qualifierValue.isEmpty()) {
                        dependency = getBean(qualifierValue);
                    } else {
                        // Otherwise, try to find by type
                        dependency = findBeanByType(field.getType());
                    }
                    
                    if (dependency == null && autowired.required()) {
                        throw new Exception("Could not autowire field: " + field.getName() + 
                                           " in " + beanClass.getName() + " - no matching bean found");
                    }
                    
                    if (dependency != null) {
                        field.set(beanInstance, dependency);
                    }
                }
            }
            
            currentClass = currentClass.getSuperclass();
        }
    }
    
    /**
     * Inject dependencies into setter methods annotated with @Autowired.
     * 
     * @param beanInstance the bean instance
     * @param beanClass the bean class
     */
    private void injectSetterDependencies(Object beanInstance, Class<?> beanClass) throws Exception {
        Method[] methods = beanClass.getMethods();
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = method.getAnnotation(Autowired.class);
                method.setAccessible(true);
                
                // Only consider setter-like methods with a single parameter
                if (method.getParameterCount() != 1) {
                    continue;
                }
                
                Parameter parameter = method.getParameters()[0];
                Class<?> parameterType = parameter.getType();
                
                // Check for @Qualifier on the method or parameter
                String qualifierValue = null;
                if (method.isAnnotationPresent(Qualifier.class)) {
                    qualifierValue = method.getAnnotation(Qualifier.class).value();
                } else if (parameter.isAnnotationPresent(Qualifier.class)) {
                    qualifierValue = parameter.getAnnotation(Qualifier.class).value();
                }
                
                Object dependency;
                // If there's a qualifier, use it to find the bean
                if (qualifierValue != null && !qualifierValue.isEmpty()) {
                    dependency = getBean(qualifierValue);
                } else {
                    // Otherwise, try to find by type
                    dependency = findBeanByType(parameterType);
                }
                
                if (dependency == null && autowired.required()) {
                    throw new Exception("Could not autowire method: " + method.getName() + 
                                       " in " + beanClass.getName() + " - no matching bean found");
                }
                
                if (dependency != null) {
                    method.invoke(beanInstance, dependency);
                }
            }
        }
    }
    
    /**
     * Find a bean by type.
     * 
     * @param requiredType the required type
     * @return the bean or null if none found
     */
    private Object findBeanByType(Class<?> requiredType) throws Exception {
        String beanName = typeToBeanNameMap.get(requiredType);
        
        if (beanName != null) {
            return getBean(beanName);
        }
        
        // If no exact match found, look for a bean that can be assigned to this type
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            try {
                Class<?> beanClass = Class.forName(entry.getValue().getClassName());
                if (requiredType.isAssignableFrom(beanClass)) {
                    return getBean(entry.getKey());
                }
            } catch (ClassNotFoundException e) {
                // Skip if class can't be loaded
            }
        }
        
        return null;
    }
    
    // BeanDefinitionRegistry implementation
    
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
        return Map.copyOf(beanDefinitionMap);
    }
}
