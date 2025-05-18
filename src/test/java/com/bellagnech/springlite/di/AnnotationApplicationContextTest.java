package com.bellagnech.springlite.di;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bellagnech.springlite.test.annotation.DependentBean;
import com.bellagnech.springlite.test.annotation.FieldInjectionBean;
import com.bellagnech.springlite.test.annotation.ProtoBean;
import com.bellagnech.springlite.test.annotation.SetterInjectionBean;
import com.bellagnech.springlite.test.annotation.SimpleService;

public class AnnotationApplicationContextTest {

    @Test
    public void testAnnotationBasedInjection() throws Exception {
        // Create context and scan the test package
        ApplicationContext context = new AnnotationApplicationContext(
                "com.bellagnech.springlite.test.annotation");
        
        // Test simple bean was discovered and instantiated
        SimpleService simpleService = (SimpleService) context.getBean("simpleService");
        assertNotNull(simpleService);
        assertEquals("Hello from SimpleService", simpleService.getMessage());
        
        // Test constructor injection
        DependentBean dependentBean = (DependentBean) context.getBean("dependentBean");
        assertNotNull(dependentBean);
        assertEquals("DependentBean says: Hello from SimpleService", dependentBean.getServiceMessage());
        
        // Test field injection
        FieldInjectionBean fieldBean = (FieldInjectionBean) context.getBean("fieldInjectionBean");
        assertNotNull(fieldBean);
        assertEquals("FieldInjectionBean says: Hello from SimpleService", fieldBean.getServiceMessage());
        
        // Test setter injection
        SetterInjectionBean setterBean = (SetterInjectionBean) context.getBean("setterInjectionBean");
        assertNotNull(setterBean);
        assertEquals("SetterInjectionBean says: Hello from SimpleService", setterBean.getServiceMessage());
        
        // Test prototype scope
        ProtoBean protoBean1 = (ProtoBean) context.getBean("protoBean");
        ProtoBean protoBean2 = (ProtoBean) context.getBean("protoBean");
        assertNotNull(protoBean1);
        assertNotNull(protoBean2);
        assertNotSame(protoBean1, protoBean2);
        assertEquals(1, protoBean1.getInstanceNumber());
        assertEquals(2, protoBean2.getInstanceNumber());
    }
}
