package com.bellagnech.springlite.di;

import com.bellagnech.springlite.di.annotation.AnnotationBeanDefinitionReader;
import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Qualifier;
import com.bellagnech.springlite.di.util.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ApplicationContext implementation that loads beans based on annotations.
 * Scans packages for classes with @Component annotation and
 * handles dependency injection based on @Autowired annotation.
 */
public class AnnotationApplicationContext implements ApplicationContext, BeanDefinitionRegistry {
    
    private static final Logger logger = Logger.getLogger(AnnotationApplicationContext.class);
    
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Map<String, Object> singletonObjects = new HashMap<>();
    private final AnnotationBeanDefinitionReader beanDefinitionReader;
    private final String[] basePackages;
    private final Set<String> currentlyCreatingBeans = new HashSet<>();
    private final Map<Class<?>, String> typeToBeanNameMap = new HashMap<>();
    
    /**
     * Create a new AnnotationApplicationContext with the given base packages to scan.
     * 
     * @param basePackages the packages to scan for annotated beans
     */
    public AnnotationApplicationContext(String... basePackages) throws Exception {
        logger.info("Initializing AnnotationApplicationContext with " + 
                   (basePackages != null ? basePackages.length : 0) + " base packages");
        this.basePackages = basePackages;
        this.beanDefinitionReader = new AnnotationBeanDefinitionReader(this);
        refresh();
    }
    
    @Override
    public void refresh() throws Exception {
        logger.info("Refreshing AnnotationApplicationContext");
        
        this.singletonObjects.clear();
        this.typeToBeanNameMap.clear();
        
        if (basePackages != null) {
            for (String basePackage : basePackages) {
                logger.debug("Scanning package: " + basePackage);
                beanDefinitionReader.scan(basePackage);
            }
        }
        
        buildTypeToBeanNameMap();
        
        validateBeanDefinitions();
        
        logger.info("AnnotationApplicationContext refresh completed with " + 
                    beanDefinitionMap.size() + " bean definitions");
    }
    
    /**
     * Validate bean definitions for correctness.
     */
    private void validateBeanDefinitions() throws BeanCreationException {
        logger.debug("Validating bean definitions");
        
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition bd = entry.getValue();
            
            // Check required fields
            if (bd.getClassName() == null || bd.getClassName().isEmpty()) {
                throw new BeanCreationException(beanName, "Bean class name is required");
            }
            
            // Validate class exists
            try {
                Class.forName(bd.getClassName());
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException(beanName, 
                    "Bean class not found: " + bd.getClassName(), e);
            }
        }
    }
    
    /**
     * Build a map of types to bean names for use in autowiring by type.
     * If multiple beans of same type exist, this will keep the last one.
     */
    private void buildTypeToBeanNameMap() throws ClassNotFoundException {
        logger.debug("Building type-to-bean-name map for autowiring");
        
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            Class<?> beanClass = Class.forName(beanDefinition.getClassName());
            
            // Map the class itself
            if (typeToBeanNameMap.containsKey(beanClass)) {
                logger.warn("Multiple beans of type " + beanClass.getName() + 
                          " found. Autowiring may be unpredictable. Consider using @Qualifier.");
            }
            typeToBeanNameMap.put(beanClass, beanName);
            
            // Map interfaces implemented by this class
            for (Class<?> interfaceClass : beanClass.getInterfaces()) {
                if (typeToBeanNameMap.containsKey(interfaceClass)) {
                    logger.warn("Multiple beans implementing " + interfaceClass.getName() + 
                              " found. Autowiring may be unpredictable. Consider using @Qualifier.");
                }
                typeToBeanNameMap.put(interfaceClass, beanName);
            }
        }
        
        logger.debug("Type-to-bean-name map built with " + typeToBeanNameMap.size() + " entries");
    }
    
    @Override
    public Object getBean(String id) throws NoSuchBeanDefinitionException, BeanCreationException {
        logger.debug("Getting bean with id: " + id);
        
        BeanDefinition beanDefinition;
        try {
            beanDefinition = getBeanDefinition(id);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("No bean definition found for: " + id);
            throw e;
        }
        
        if ("prototype".equals(beanDefinition.getScope())) {
            logger.debug("Creating new prototype instance for bean: " + id);
            try {
                return createBean(beanDefinition);
            } catch (CircularDependencyException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Error creating prototype bean: " + id, e);
                throw new BeanCreationException(id, "Error creating prototype bean", e);
            }
        }
        
        Object singleton = this.singletonObjects.get(id);
        if (singleton == null) {
            logger.debug("Creating singleton instance for bean: " + id);
            try {
                singleton = createBean(beanDefinition);
                this.singletonObjects.put(id, singleton);
            } catch (CircularDependencyException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Error creating singleton bean: " + id, e);
                throw new BeanCreationException(id, "Error creating singleton bean", e);
            }
        } else {
            logger.debug("Returning existing singleton instance for bean: " + id);
        }
        
        return singleton;
    }
    
    @Override
    public <T> T getBean(String id, Class<T> requiredType) throws NoSuchBeanDefinitionException, BeanCreationException {
        Object bean = getBean(id);
        
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw new BeanCreationException(id, 
                "Bean is not of required type " + requiredType.getName() + 
                ", actual type is " + bean.getClass().getName());
        }
        
        return requiredType.cast(bean);
    }
    
    @Override
    public boolean containsBean(String id) {
        return containsBeanDefinition(id);
    }
    
    /**
     * Create a new bean instance from the given bean definition.
     * 
     * @param beanDefinition the bean definition
     * @return the bean instance
     */
    protected Object createBean(BeanDefinition beanDefinition) throws Exception {
        String beanId = beanDefinition.getId();
        logger.debug("Creating bean: " + beanId);
        
        if (currentlyCreatingBeans.contains(beanId)) {
            logger.error("Circular reference detected for bean: " + beanId);
            throw new CircularDependencyException(beanId, new HashSet<>(currentlyCreatingBeans));
        }
        
        try {
            currentlyCreatingBeans.add(beanId);
            
            Class<?> beanClass = Class.forName(beanDefinition.getClassName());
            logger.debug("Loaded class: " + beanClass.getName());
            
            Object beanInstance = instantiateBean(beanClass);
            logger.debug("Instantiated bean: " + beanId);
            
            injectFieldDependencies(beanInstance, beanClass);
            injectSetterDependencies(beanInstance, beanClass);
            logger.debug("Injected dependencies for bean: " + beanId);
            
            return beanInstance;
        } finally {
            currentlyCreatingBeans.remove(beanId);
        }
    }
    
    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) throws BeanCreationException {
        String beanId = beanDefinition.getId();
        if (beanId == null || beanId.isEmpty()) {
            throw new BeanCreationException(beanId, "Bean ID cannot be null or empty");
        }
        
        if (containsBeanDefinition(beanId)) {
            logger.warn("Overriding bean definition for bean '" + beanId + "'");
        }
        
        logger.debug("Registering bean definition: " + beanId);
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
        return Map.copyOf(beanDefinitionMap);
    }
    
    /* Keep the existing methods below unchanged */
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
}
