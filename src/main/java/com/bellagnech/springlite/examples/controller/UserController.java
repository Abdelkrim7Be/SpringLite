package com.bellagnech.springlite.examples.controller;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;
import com.bellagnech.springlite.examples.model.User;
import com.bellagnech.springlite.examples.service.NotificationService;
import com.bellagnech.springlite.examples.service.UserService;

/**
 * Controller for user-related operations.
 * Demonstrates field-based dependency injection.
 */
@Component
public class UserController {
    
    @Autowired
    private UserService userService;
    
    public User getUser(String username) {
        return userService.getUserByUsername(username);
    }
    
    public User registerUser(String username, String fullName, String email) {
        User user = userService.createUser(username, fullName, email);
        
        // Get a new instance of NotificationService (prototype bean)
        return user;
    }
    
    public void printAllUsers() {
        System.out.println("All Users:");
        for (User user : userService.getAllUsers()) {
            System.out.println(" - " + user);
        }
    }
}
