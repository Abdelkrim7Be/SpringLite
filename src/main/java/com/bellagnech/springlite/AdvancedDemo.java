package com.bellagnech.springlite;

import com.bellagnech.springlite.di.AnnotationApplicationContext;
import com.bellagnech.springlite.di.ApplicationContext;
import com.bellagnech.springlite.di.BeanCreationException;
import com.bellagnech.springlite.di.NoSuchBeanDefinitionException;
import com.bellagnech.springlite.di.util.Logger;

/**
 * Advanced demo application showcasing error handling and logging.
 */
public class AdvancedDemo {
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setLevel(Logger.Level.DEBUG);
        Logger logger = Logger.getLogger(AdvancedDemo.class);
        
        try {
            logger.info("Starting advanced demo application");
            
            // Create annotation-based application context
            ApplicationContext context = new AnnotationApplicationContext(
                    "com.bellagnech.springlite.test.advanced");
            
            logger.info("Application context successfully initialized");
            
            // Get beans and show information
            String[] beanNames = ((AnnotationApplicationContext) context).getBeanDefinitionNames();
            logger.info("Discovered beans:");
            for (String beanName : beanNames) {
                try {
                    Object bean = context.getBean(beanName);
                    logger.info("- " + beanName + ": " + bean.getClass().getName());
                } catch (Exception e) {
                    logger.error("Error getting bean: " + beanName, e);
                }
            }
            
            // Demonstrate error handling with non-existent bean
            try {
                context.getBean("nonExistentBean");
            } catch (NoSuchBeanDefinitionException e) {
                logger.info("Successfully caught NoSuchBeanDefinitionException: " + e.getMessage());
            }
            
            // Demonstrate type checking
            try {
                // This should fail with type mismatch
                context.getBean("serviceA", String.class);
            } catch (BeanCreationException e) {
                logger.info("Successfully caught BeanCreationException: " + e.getMessage());
            }
            
            logger.info("Advanced demo completed successfully");
            
        } catch (Exception e) {
            logger.error("Demo failed with exception", e);
        }
    }
}
