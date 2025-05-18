package com.bellagnech.springlite.examples.repository;

import com.bellagnech.springlite.di.annotations.Component;
import com.bellagnech.springlite.examples.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of the UserRepository.
 */
@Component
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> usersByUsername = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public InMemoryUserRepository() {
        // Add some sample users
        save(new User(null, "johndoe", "John Doe", "john@example.com"));
        save(new User(null, "janedoe", "Jane Doe", "jane@example.com"));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersByUsername.values());
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        usersByUsername.put(user.getUsername(), user);
        return user;
    }
}
