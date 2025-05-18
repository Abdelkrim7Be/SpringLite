package com.bellagnech.springlite.di;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bellagnech.springlite.di.util.Logger;
import com.bellagnech.springlite.test.lifecycle.CircularDependencyA;
import com.bellagnech.springlite.test.lifecycle.LifecycleBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AdvancedErrorHandlingTest {

    @Test
    public void testBeanNotFound() {
        try {
            // Enable debug logging
            Logger.setLevel(Logger.Level.DEBUG);
            
            // Create an application context
            ApplicationContext context = new AnnotationApplicationContext(
                    "com.bellagnech.springlite.test.lifecycle");
            
            // Try to get a non-existent bean
            Exception exception = assertThrows(NoSuchBeanDefinitionException.class, () -> {
                context.getBean("nonExistentBean");
            });
            
            assertEquals("No bean definition found for bean ID: nonExistentBean", 
                         exception.getMessage());
            
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testCircularDependencyDetection() {
        try {
            // Create an application context with circular dependencies
            Exception exception = assertThrows(CircularDependencyException.class, () -> {
                ApplicationContext context = new AnnotationApplicationContext(
                        "com.bellagnech.springlite.test.lifecycle");
                
                // Trying to get either bean should trigger circular dependency detection
                context.getBean("circularDependencyA");
            });
            
            assertTrue(exception.getMessage().contains("Circular reference detected"));
            
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testInvalidBeanDefinition() {
        try {
            // Create invalid XML bean definition
            String invalidXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<beans>" +
                    "    <bean id=\"missingClassBean\" />" + // Missing class attribute
                    "</beans>";
            
            InputStream inputStream = new ByteArrayInputStream(
                    invalidXml.getBytes(StandardCharsets.UTF_8));
            
            // This should fail with a BeanCreationException
            assertThrows(BeanCreationException.class, () -> {
                new XmlApplicationContext(inputStream);
            });
            
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testInvalidBeanReference() {
        try {
            // Create XML with invalid reference
            String invalidRefXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<beans>" +
                    "    <bean id=\"beanWithInvalidRef\" class=\"java.lang.Object\">" +
                    "        <property name=\"someProperty\" ref=\"nonExistentBean\" />" +
                    "    </bean>" +
                    "</beans>";
            
            InputStream inputStream = new ByteArrayInputStream(
                    invalidRefXml.getBytes(StandardCharsets.UTF_8));
            
            // This should fail with a BeanCreationException
            assertThrows(BeanCreationException.class, () -> {
                new XmlApplicationContext(inputStream);
            });
            
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testBeanTypeMismatch() {
        try {
            // Create context
            ApplicationContext context = new AnnotationApplicationContext(
                    "com.bellagnech.springlite.test.lifecycle");
            
            // Try to get a bean with the wrong type
            Exception exception = assertThrows(BeanCreationException.class, () -> {
                context.getBean("lifecycleBean", CircularDependencyA.class);
            });
            
            assertTrue(exception.getMessage().contains("Bean is not of required type"), 
                      "Expected type mismatch error but got: " + exception.getMessage());
            
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
}
