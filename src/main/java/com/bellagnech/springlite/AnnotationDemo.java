package com.bellagnech.springlite;

import com.bellagnech.springlite.di.AnnotationApplicationContext;
import com.bellagnech.springlite.di.ApplicationContext;

/**
 * Demo class to show annotation-based dependency injection in action.
 */
public class AnnotationDemo {
    
    public static void main(String[] args) {
        try {
            // Create the application context with the package to scan
            ApplicationContext context = new AnnotationApplicationContext("com.bellagnech.springlite.test.annotation");
            
            // Print the names of all beans found
            System.out.println("\nDiscovered beans:");
            String[] beanNames = ((AnnotationApplicationContext) context).getBeanDefinitionNames();
            for (String beanName : beanNames) {
                System.out.println("- " + beanName);
            }
            
            // Get beans and demonstrate different injection types
            System.out.println("\nGetting and using beans:");
            
            Object dependentBean = context.getBean("dependentBean");
            System.out.println("dependentBean: " + dependentBean);
            
            Object fieldInjectionBean = context.getBean("fieldInjectionBean");
            System.out.println("fieldInjectionBean: " + fieldInjectionBean);
            
            Object setterInjectionBean = context.getBean("setterInjectionBean");
            System.out.println("setterInjectionBean: " + setterInjectionBean);
            
            // Demonstrate prototype scope
            System.out.println("\nDemonstrating prototype scope:");
            System.out.println("protoBean 1: " + context.getBean("protoBean"));
            System.out.println("protoBean 2: " + context.getBean("protoBean"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
