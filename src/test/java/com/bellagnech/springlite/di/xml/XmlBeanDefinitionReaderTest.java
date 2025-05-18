package com.bellagnech.springlite.di.xml;

import com.bellagnech.springlite.di.BeanDefinition;
import com.bellagnech.springlite.di.BeanDefinitionRegistry;
import com.bellagnech.springlite.di.DefaultBeanDefinitionRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class XmlBeanDefinitionReaderTest {
    
    private XmlBeanDefinitionReader reader;
    private BeanDefinitionRegistry registry;
    
    @BeforeEach
    public void setUp() {
        registry = new DefaultBeanDefinitionRegistry();
        reader = new XmlBeanDefinitionReader(registry);
    }
    
    @Test
    public void testLoadBeanDefinitions() throws Exception {
        // Load test beans.xml from classpath
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("beans.xml");
        reader.loadBeanDefinitions(inputStream);
        
        // Verify the beans were loaded correctly
        Map<String, BeanDefinition> beanDefinitions = registry.getBeanDefinitions();
        
        assertEquals(5, beanDefinitions.size(), "Should have loaded 5 bean definitions");
        
        // Check individual beans
        assertTrue(registry.containsBeanDefinition("simpleBean"), "Should contain simpleBean");
        assertEquals("com.bellagnech.springlite.test.SimpleBean", 
                registry.getBeanDefinition("simpleBean").getClassName());
        
        assertTrue(registry.containsBeanDefinition("prototypeBean"), "Should contain prototypeBean");
        assertEquals("prototype", 
                registry.getBeanDefinition("prototypeBean").getScope(), 
                "prototypeBean should have prototype scope");
        
        // Check singleton scope is default
        assertEquals("singleton", 
                registry.getBeanDefinition("simpleBean").getScope(), 
                "Default scope should be singleton");
    }
}
