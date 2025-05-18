package com.bellagnech.springlite;

import com.bellagnech.springlite.di.ApplicationContext;
import com.bellagnech.springlite.di.XmlApplicationContext;

/**
 * Simple application to demonstrate XML-based dependency injection.
 */
public class App {
    
    public static void main(String[] args) {
        try {
            // Load the application context from the XML configuration
            ApplicationContext context = new XmlApplicationContext("src/test/resources/beans.xml");
            
            // Get and use beans
            Object simpleBean = context.getBean("simpleBean");
            System.out.println("Simple bean: " + simpleBean);
            
            Object setterInjectionBean = context.getBean("setterInjectionBean");
            System.out.println("Setter injected bean: " + setterInjectionBean);
            
            Object valueBean = context.getBean("valueBean");
            System.out.println("Value bean: " + valueBean);
            
            // Test prototype scope
            System.out.println("Creating prototype beans...");
            System.out.println("Prototype 1: " + context.getBean("prototypeBean"));
            System.out.println("Prototype 2: " + context.getBean("prototypeBean"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
