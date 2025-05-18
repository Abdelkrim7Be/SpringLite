package com.bellagnech.springlite.examples.repository;

import com.bellagnech.springlite.examples.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entities.
 */
public interface UserRepository {
    
    /**
     * Find a user by their username.
     * 
     * @param username the username to search for
     * @return the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Get all users.
     * 
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Save a user.
     * 
     * @param user the user to save
     * @return the saved user with generated ID
     */
    User save(User user);
}
