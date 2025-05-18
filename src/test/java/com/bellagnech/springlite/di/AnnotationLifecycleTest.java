package com.bellagnech.springlite.di;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.bellagnech.springlite.test.lifecycle.LifecycleBean;
import com.bellagnech.springlite.test.advanced.ServiceA;
import com.bellagnech.springlite.test.advanced.ServiceB;
import com.bellagnech.springlite.test.advanced.ServiceC;

public class AnnotationLifecycleTest {

    @BeforeEach
    public void setUp() {
        LifecycleBean.clearEvents();
    }

    @Test
    public void testBeanLifecycle() throws Exception {
        // Create context and scan the test package
        ApplicationContext context = new AnnotationApplicationContext(
                "com.bellagnech.springlite.test.lifecycle");
        
        // Verify lifecycle events were recorded
        assertNotNull(context.getBean("lifecycleBean"));
        assertFalse(LifecycleBean.getEvents().isEmpty());
        assertTrue(LifecycleBean.getEvents().contains("Constructor called"));
    }
    
    @Test
    public void testCircularDependencyDetection() {
        // Create context with circular dependencies
        assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                ApplicationContext context = new AnnotationApplicationContext(
                        "com.bellagnech.springlite.test.lifecycle");
                
                // Trying to get either bean should trigger circular dependency detection
                context.getBean("circularDependencyA");
            }
        });
    }
    
    @Test
    public void testComplexDependencyGraph() throws Exception {
        // Create context and scan the test package
        ApplicationContext context = new AnnotationApplicationContext(
                "com.bellagnech.springlite.test.advanced");
        
        // Get ServiceC which depends on both ServiceA and ServiceB
        ServiceC serviceC = (ServiceC) context.getBean("serviceC");
        assertNotNull(serviceC);
        
        // Verify the dependency graph was properly constructed
        String message = serviceC.getCompleteMessage();
        assertTrue(message.contains("ServiceA #1"));
        assertTrue(message.contains("ServiceB using ServiceA #1"));
        
        // Verify that the same ServiceA instance was injected into both ServiceB and ServiceC
        ServiceA serviceA = (ServiceA) context.getBean("serviceA");
        ServiceB serviceB = (ServiceB) context.getBean("serviceB");
        
        // Should be same instances (singleton behavior)
        assertEquals(serviceA.getName(), "ServiceA #1");
        assertEquals(serviceB.getMessage(), "ServiceB using ServiceA #1");
    }
}
