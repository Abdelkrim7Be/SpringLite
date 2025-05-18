package com.bellagnech.springlite.examples;

import com.bellagnech.springlite.di.ApplicationContext;
import com.bellagnech.springlite.di.XmlApplicationContext;
import com.bellagnech.springlite.di.util.Logger;
import com.bellagnech.springlite.examples.model.User;
import com.bellagnech.springlite.examples.service.NotificationService;
import com.bellagnech.springlite.examples.service.UserService;

/**
 * Example application using XML-based configuration.
 */
public class XmlConfigExample {
    
    public static void main(String[] args) {
        // Set up logging
        Logger.setLevel(Logger.Level.INFO);
        
        try {
            System.out.println("Starting SpringLite XML Configuration Example");
            System.out.println("==============================================");
            
            // Create the application context with XML configuration
            ApplicationContext context = new XmlApplicationContext(
                    "src/main/resources/example-beans.xml");
            
            // Get the user service from the context
            UserService userService = context.getBean("userService", UserService.class);
            
            // Display existing users
            System.out.println("\nExisting users:");
            for (User user : userService.getAllUsers()) {
                System.out.println(" - " + user);
            }
            
            // Create a new user
            System.out.println("\nCreating a new user:");
            User newUser = userService.createUser("bobsmith", "Bob Smith", "bob@example.com");
            System.out.println(" - Created: " + newUser);
            
            // Get prototype-scoped notification service instances
            System.out.println("\nDemonstrating prototype scope:");
            NotificationService notification1 = context.getBean("notificationService", NotificationService.class);
            notification1.sendWelcomeEmail(newUser);
            
            NotificationService notification2 = context.getBean("notificationService", NotificationService.class);
            notification2.sendWelcomeEmail(newUser);
            
            System.out.println("Same instance? " + (notification1 == notification2)); // Should be false
            
            System.out.println("\nXML Configuration Example completed successfully");
            
        } catch (Exception e) {
            System.err.println("Error in XML example: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
