package com.bellagnech.springlite.examples.service;

import com.bellagnech.springlite.examples.model.User;
import java.util.List;

/**
 * Service interface for User operations.
 */
public interface UserService {
    
    /**
     * Get a user by username.
     * 
     * @param username the username to search for
     * @return the user if found, or null if not found
     */
    User getUserByUsername(String username);
    
    /**
     * Get all users.
     * 
     * @return list of all users
     */
    List<User> getAllUsers();
    
    /**
     * Create a new user.
     * 
     * @param username the username
     * @param fullName the full name
     * @param email the email address
     * @return the created user
     */
    User createUser(String username, String fullName, String email);
}
