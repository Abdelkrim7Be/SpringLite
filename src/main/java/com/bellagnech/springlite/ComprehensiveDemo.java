package com.bellagnech.springlite;

import com.bellagnech.springlite.di.AnnotationApplicationContext;
import com.bellagnech.springlite.di.ApplicationContext;
import com.bellagnech.springlite.di.XmlApplicationContext;

/**
 * Comprehensive demo showing both XML and annotation-based dependency injection.
 */
public class ComprehensiveDemo {
    
    public static void main(String[] args) {
        try {
            System.out.println("===== XML-BASED CONFIGURATION DEMO =====");
            demoXmlConfig();
            
            System.out.println("\n\n===== ANNOTATION-BASED CONFIGURATION DEMO =====");
            demoAnnotationConfig();
            
        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void demoXmlConfig() throws Exception {
        // Create XML-based application context
        ApplicationContext context = new XmlApplicationContext("src/test/resources/beans.xml");
        
        // Get beans from context and use them
        System.out.println("SimpleBean: " + context.getBean("simpleBean"));
        System.out.println("SetterInjectionBean: " + context.getBean("setterInjectionBean"));
        System.out.println("ValueBean: " + context.getBean("valueBean"));
        
        // Test prototype scope
        Object proto1 = context.getBean("prototypeBean");
        Object proto2 = context.getBean("prototypeBean");
        System.out.println("Prototype beans same instance? " + (proto1 == proto2));
    }
    
    private static void demoAnnotationConfig() throws Exception {
        // Create annotation-based application context
        ApplicationContext context = new AnnotationApplicationContext(
                "com.bellagnech.springlite.test.annotation",
                "com.bellagnech.springlite.test.advanced");
        
        // Get beans from context and use them
        System.out.println("Found beans:");
        
        // Simple service with no dependencies
        Object simpleService = context.getBean("simpleService");
        System.out.println("- simpleService: " + simpleService);
        
        // Constructor injected bean
        Object dependentBean = context.getBean("dependentBean");
        System.out.println("- dependentBean: " + dependentBean);
        
        // Field injected bean
        Object fieldInjectionBean = context.getBean("fieldInjectionBean");
        System.out.println("- fieldInjectionBean: " + fieldInjectionBean);
        
        // Setter injected bean
        Object setterInjectionBean = context.getBean("setterInjectionBean");
        System.out.println("- setterInjectionBean: " + setterInjectionBean);
        
        // Complex dependency graph
        Object serviceC = context.getBean("serviceC");
        System.out.println("- serviceC: " + serviceC);
        
        // Test prototype scope
        Object proto1 = context.getBean("protoBean");
        Object proto2 = context.getBean("protoBean");
        System.out.println("Prototype beans same instance? " + (proto1 == proto2));
    }
}
