package com.bellagnech.springlite.examples.service;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;
import com.bellagnech.springlite.examples.model.User;
import com.bellagnech.springlite.examples.repository.UserRepository;

import java.util.List;

/**
 * Implementation of the UserService interface.
 */
@Component("userService")
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("UserServiceImpl created with " + userRepository.getClass().getSimpleName());
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User createUser(String username, String fullName, String email) {
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        
        return userRepository.save(user);
    }
}
