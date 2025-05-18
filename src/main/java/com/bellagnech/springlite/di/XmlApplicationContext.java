package com.bellagnech.springlite.di;

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
        beanDefinitionReader.loadBeanDefinitions(inputStream);
    }
    
    @Override
    public void refresh() throws Exception {
        // Clear the singleton cache
        this.singletonObjects.clear();
        
        // Load bean definitions from XML config files
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                beanDefinitionReader.loadBeanDefinitions(configLocation);
            }
        }
        
        // Instantiate all singleton beans
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                getBean(beanName);
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
            
            // Create a new instance
            Object beanInstance = instantiateBean(beanClass);
            
            // Inject dependencies
            injectDependencies(beanInstance, beanDefinition);
            
            return beanInstance;
        } finally {
            // Remove from currently creating beans
            currentlyCreatingBeans.remove(beanId);
        }
    }
    
    /**
     * Instantiate a bean of the given class.
     * 
     * @param beanClass the bean class
     * @return the bean instance
     */
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
    
    /**
     * Inject dependencies into the bean instance.
     * 
     * @param beanInstance the bean instance
     * @param beanDefinition the bean definition
     */
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
    
    /**
     * Inject a dependency using a setter method.
     * 
     * @param beanInstance the bean instance
     * @param propertyName the property name
     * @param value the property value
     * @return true if injection was successful
     */
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
    
    /**
     * Inject a dependency using field injection.
     * 
     * @param beanInstance the bean instance
     * @param propertyName the property name
     * @param value the property value
     */
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
    
    /**
     * Find a field with the given name in the class hierarchy.
     * 
     * @param clazz the class to search
     * @param fieldName the field name
     * @return the field or null if not found
     */
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
    
    /**
     * Convert the value to the required type if needed.
     * 
     * @param value the original value
     * @param requiredType the required type
     * @return the converted value
     */
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
