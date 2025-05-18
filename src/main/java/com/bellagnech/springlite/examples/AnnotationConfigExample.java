package com.bellagnech.springlite.examples;

import com.bellagnech.springlite.di.AnnotationApplicationContext;
import com.bellagnech.springlite.di.ApplicationContext;
import com.bellagnech.springlite.di.util.Logger;
import com.bellagnech.springlite.examples.controller.UserController;
import com.bellagnech.springlite.examples.model.User;
import com.bellagnech.springlite.examples.service.NotificationService;
import com.bellagnech.springlite.examples.service.UserService;

/**
 * Example application using annotation-based configuration.
 */
public class AnnotationConfigExample {
    
    public static void main(String[] args) {
        // Set up logging
        Logger.setLevel(Logger.Level.INFO);
        
        try {
            System.out.println("Starting SpringLite Annotation Configuration Example");
            System.out.println("==================================================");
            
            // Create the application context with annotation-based configuration
            ApplicationContext context = new AnnotationApplicationContext(
                    "com.bellagnech.springlite.examples");
            
            // Get the user controller from the context
            UserController userController = context.getBean("userController", UserController.class);
            
            // Display existing users
            System.out.println("\nExisting users:");
            userController.printAllUsers();
            
            // Create a new user
            System.out.println("\nCreating a new user:");
            User newUser = userController.registerUser("alicesmith", "Alice Smith", "alice@example.com");
            System.out.println(" - Created: " + newUser);
            
            // Get prototype-scoped notification service instances
            System.out.println("\nDemonstrating prototype scope:");
            NotificationService notification1 = context.getBean("notificationService", NotificationService.class);
            notification1.sendWelcomeEmail(newUser);
            
            NotificationService notification2 = context.getBean("notificationService", NotificationService.class);
            notification2.sendWelcomeEmail(newUser);
            
            System.out.println("notification1 instance ID: " + notification1.getInstanceId());
            System.out.println("notification2 instance ID: " + notification2.getInstanceId());
            System.out.println("Same instance? " + (notification1 == notification2)); // Should be false
            
            // Find a user
            System.out.println("\nLooking up user 'johndoe':");
            User johnDoe = userController.getUser("johndoe");
            System.out.println(" - Found: " + johnDoe);
            
            System.out.println("\nAnnotation Configuration Example completed successfully");
            
        } catch (Exception e) {
            System.err.println("Error in annotation example: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
