package com.bellagnech.springlite.di;

import com.bellagnech.springlite.test.*;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class XmlApplicationContextTest {

    @Test
    public void testBeanInstantiationAndInjection() throws Exception {
        // Load the application context from the test XML file
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("beans.xml");
        ApplicationContext context = new XmlApplicationContext(inputStream);
        
        // Test simple bean
        SimpleBean simpleBean = (SimpleBean) context.getBean("simpleBean");
        assertNotNull(simpleBean);
        assertEquals("Hello from XML configuration!", simpleBean.getMessage());
        
        // Test setter injection
        SetterInjectionBean setterBean = (SetterInjectionBean) context.getBean("setterInjectionBean");
        assertNotNull(setterBean);
        assertNotNull(setterBean.getDependency());
        assertSame(simpleBean, setterBean.getDependency());
        
        // Test field injection
        FieldInjectionBean fieldBean = (FieldInjectionBean) context.getBean("fieldInjectionBean");
        assertNotNull(fieldBean);
        assertNotNull(fieldBean.getDependency());
        assertSame(simpleBean, fieldBean.getDependency());
        
        // Test value injection
        ValueBean valueBean = (ValueBean) context.getBean("valueBean");
        assertNotNull(valueBean);
        assertEquals("Hello, Spring Lite!", valueBean.getStringValue());
        assertEquals(42, valueBean.getIntValue());
        
        // Test prototype scope
        PrototypeBean prototype1 = (PrototypeBean) context.getBean("prototypeBean");
        PrototypeBean prototype2 = (PrototypeBean) context.getBean("prototypeBean");
        assertNotNull(prototype1);
        assertNotNull(prototype2);
        assertNotSame(prototype1, prototype2);
        assertEquals(1, prototype1.getInstanceNumber());
        assertEquals(2, prototype2.getInstanceNumber());
    }
}
