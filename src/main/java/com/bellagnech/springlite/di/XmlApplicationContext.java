package com.bellagnech.springlite.di;

import com.bellagnech.springlite.di.util.Logger;
import com.bellagnech.springlite.di.xml.XmlBeanDefinitionReader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ApplicationContext implementation based on XML configuration.
 * Handles bean instantiation and dependency injection from XML configuration.
 */
public class XmlApplicationContext implements ApplicationContext, BeanDefinitionRegistry {
    
    private static final Logger logger = Logger.getLogger(XmlApplicationContext.class);
    
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Map<String, Object> singletonObjects = new HashMap<>();
    private final XmlBeanDefinitionReader beanDefinitionReader;
    private final String[] configLocations;
    private final Set<String> currentlyCreatingBeans = new HashSet<>(); // For circular dependency detection
    
    /**
     * Create a new XmlApplicationContext with the given configuration locations.
     * 
     * @param configLocations the XML configuration file paths
     */
    public XmlApplicationContext(String... configLocations) throws Exception {
        logger.info("Initializing XmlApplicationContext with " + 
                   (configLocations != null ? configLocations.length : 0) + " config locations");
        this.configLocations = configLocations;
        this.beanDefinitionReader = new XmlBeanDefinitionReader(this);
        refresh();
    }
    
    /**
     * Create a new XmlApplicationContext with the given input stream.
     * 
     * @param inputStream the XML configuration input stream
     */
    public XmlApplicationContext(InputStream inputStream) throws Exception {
        logger.info("Initializing XmlApplicationContext with input stream");
        this.configLocations = null;
        this.beanDefinitionReader = new XmlBeanDefinitionReader(this);
        loadBeanDefinitions(inputStream);
        refresh();
    }
    
    /**
     * Load bean definitions from the input stream.
     * 
     * @param inputStream the XML configuration input stream
     */
    private void loadBeanDefinitions(InputStream inputStream) throws Exception {
        logger.debug("Loading bean definitions from input stream");
        beanDefinitionReader.loadBeanDefinitions(inputStream);
    }
    
    @Override
    public void refresh() throws Exception {
        logger.info("Refreshing XmlApplicationContext");
        
        // Clear the singleton cache
        this.singletonObjects.clear();
        
        // Load bean definitions from XML config files
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                logger.debug("Loading bean definitions from location: " + configLocation);
                beanDefinitionReader.loadBeanDefinitions(configLocation);
            }
        }
        
        // Validate bean definitions
        validateBeanDefinitions();
        
        // Instantiate all singleton beans
        logger.info("Instantiating singleton beans");
        String[] beanNames = getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                try {
                    getBean(beanName);
                } catch (BeanCreationException e) {
                    logger.error("Error creating singleton bean '" + beanName + "'", e);
                    throw e;
                }
            }
        }
        
        logger.info("XmlApplicationContext refresh completed with " + beanDefinitionMap.size() + " bean definitions");
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
            
            // Validate property references
            for (PropertyValue pv : bd.getPropertyValues()) {
                if (pv.isRef()) {
                    String refBeanName = (String) pv.getValue();
                    if (!containsBeanDefinition(refBeanName)) {
                        throw new BeanCreationException(beanName, 
                            "Property '" + pv.getName() + "' references undefined bean: " + refBeanName);
                    }
                }
            }
        }
    }
    
    @Override
    public Object getBean(String id) throws NoSuchBeanDefinitionException, BeanCreationException {
        logger.debug("Getting bean with id: " + id);
        
        // Check if bean definition exists
        BeanDefinition beanDefinition;
        try {
            beanDefinition = getBeanDefinition(id);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("No bean definition found for: " + id);
            throw e;
        }
        
        // If bean is a prototype, always create a new instance
        if ("prototype".equals(beanDefinition.getScope())) {
            logger.debug("Creating new prototype instance for bean: " + id);
            try {
                return createBean(beanDefinition);
            } catch (Exception e) {
                logger.error("Error creating prototype bean: " + id, e);
                throw new BeanCreationException(id, "Error creating prototype bean", e);
            }
        }
        
        // For singleton beans, check if already instantiated
        Object singleton = this.singletonObjects.get(id);
        if (singleton == null) {
            logger.debug("Creating singleton instance for bean: " + id);
            try {
                singleton = createBean(beanDefinition);
                this.singletonObjects.put(id, singleton);
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
        
        // Check for circular dependencies
        if (currentlyCreatingBeans.contains(beanId)) {
            logger.error("Circular reference detected for bean: " + beanId);
            throw new CircularDependencyException(beanId, new HashSet<>(currentlyCreatingBeans));
        }
        
        try {
            // Mark this bean as currently being created
            currentlyCreatingBeans.add(beanId);
            
            // Load the bean class
            Class<?> beanClass = Class.forName(beanDefinition.getClassName());
            logger.debug("Loaded class: " + beanClass.getName());
            
            // Create a new instance
            Object beanInstance = instantiateBean(beanClass);
            logger.debug("Instantiated bean: " + beanId);
            
            // Inject dependencies
            injectDependencies(beanInstance, beanDefinition);
            logger.debug("Injected dependencies for bean: " + beanId);
            
            return beanInstance;
        } finally {
            // Remove from currently creating beans
            currentlyCreatingBeans.remove(beanId);
        }
    }
    
    // BeanDefinitionRegistry implementation
    
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
    
    private Object instantiateBean(Class<?> beanClass) throws Exception {
        try {
            // Try to get the default constructor
            Constructor<?> constructor = beanClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            // If no default constructor, find the first available constructor
            Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
            if (constructors.length > 0) {
                // TODO: Support constructor injection with arguments
                throw new Exception("No default constructor found for " + beanClass.getName() + 
                                   ". Constructor injection not yet supported.");
            } else {
                throw new Exception("No constructors found for " + beanClass.getName());
            }
        }
    }
    
    private void injectDependencies(Object beanInstance, BeanDefinition beanDefinition) throws Exception {
        for (PropertyValue propertyValue : beanDefinition.getPropertyValues()) {
            String propertyName = propertyValue.getName();
            Object value = propertyValue.getValue();
            boolean isRef = propertyValue.isRef();
            
            // If this is a reference, get the bean it refers to
            if (isRef) {
                value = getBean((String) value);
            }
            
            // Try setter injection first
            boolean injected = injectBySetterMethod(beanInstance, propertyName, value);
            
            // If no setter found, try field injection
            if (!injected) {
                injectByField(beanInstance, propertyName, value);
            }
        }
    }
    
    private boolean injectBySetterMethod(Object beanInstance, String propertyName, Object value) throws Exception {
        Class<?> beanClass = beanInstance.getClass();
        
        // Construct the setter method name
        String setterMethodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        
        try {
            // Find setter methods
            Method[] methods = beanClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterMethodName) && method.getParameterCount() == 1) {
                    // Convert value if needed
                    Class<?> paramType = method.getParameterTypes()[0];
                    Object convertedValue = convertValueIfNeeded(value, paramType);
                    
                    // Invoke the setter
                    method.invoke(beanInstance, convertedValue);
                    return true;
                }
            }
        } catch (Exception e) {
            throw new Exception("Error injecting property " + propertyName + " by setter", e);
        }
        
        return false;
    }
    
    private void injectByField(Object beanInstance, String propertyName, Object value) throws Exception {
        Class<?> beanClass = beanInstance.getClass();
        
        try {
            // Look for field with matching name
            Field field = findField(beanClass, propertyName);
            
            if (field != null) {
                field.setAccessible(true);
                
                // Convert value if needed
                Object convertedValue = convertValueIfNeeded(value, field.getType());
                
                // Set the field value
                field.set(beanInstance, convertedValue);
            } else {
                throw new Exception("No setter method or field found for property: " + propertyName);
            }
        } catch (Exception e) {
            throw new Exception("Error injecting property " + propertyName + " by field", e);
        }
    }
    
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> searchType = clazz;
        while (searchType != null && !Object.class.equals(searchType)) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }
    
    private Object convertValueIfNeeded(Object value, Class<?> requiredType) {
        if (value == null || requiredType.isInstance(value)) {
            return value;
        }
        
        if (value instanceof String) {
            String stringValue = (String) value;
            
            // Convert string to primitive or wrapper type
            if (requiredType == Integer.class || requiredType == int.class) {
                return Integer.parseInt(stringValue);
            } else if (requiredType == Long.class || requiredType == long.class) {
                return Long.parseLong(stringValue);
            } else if (requiredType == Double.class || requiredType == double.class) {
                return Double.parseDouble(stringValue);
            } else if (requiredType == Float.class || requiredType == float.class) {
                return Float.parseFloat(stringValue);
            } else if (requiredType == Boolean.class || requiredType == boolean.class) {
                return Boolean.parseBoolean(stringValue);
            } else if (requiredType == Short.class || requiredType == short.class) {
                return Short.parseShort(stringValue);
            } else if (requiredType == Byte.class || requiredType == byte.class) {
                return Byte.parseByte(stringValue);
            } else if (requiredType == Character.class || requiredType == char.class) {
                if (stringValue.length() > 0) {
                    return stringValue.charAt(0);
                }
            }
        }
        
        // If no conversion was possible, return the original value
        return value;
    }
}
