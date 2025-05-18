package com.bellagnech.springlite.examples.service;

import com.bellagnech.springlite.di.annotations.Component;
import com.bellagnech.springlite.di.annotations.Scope;
import com.bellagnech.springlite.examples.model.User;

/**
 * Service for sending notifications. 
 * This is a prototype-scoped bean to demonstrate creating a new instance per request.
 */
@Component
@Scope("prototype")
public class NotificationService {
    
    private static int instanceCounter = 0;
    private final int instanceId;
    
    public NotificationService() {
        this.instanceId = ++instanceCounter;
        System.out.println("NotificationService instance #" + instanceId + " created");
    }
    
    public void sendWelcomeEmail(User user) {
        System.out.println("Instance #" + instanceId + " - Sending welcome email to " + user.getEmail());
        // Email sending logic would go here
    }
    
    public int getInstanceId() {
        return instanceId;
    }
}
