package com.dietapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dietapp.model.User;

public class UserService {
    private List<User> users = new ArrayList<>();

    // Create a new user
    public String createUser(User user) {
        // Validate required fields
        if(user.getName() == null || user.getName().isEmpty() ||
           user.getEmail() == null || user.getEmail().isEmpty() ||
           user.getAge() <= 0 || user.getWeight() <= 0) {
            return "Please fill in all required fields";
        }

        // Validate email format
        if(!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Invalid email format";
        }

        // Check for duplicate email
        for(User existing : users) {
            if(existing.getEmail().equals(user.getEmail())) {
                return "Email address already in use";
            }
        }

        // Assign unique ID
        user.setId(UUID.randomUUID().toString());

        // Save user
        users.add(user);

        return "Profile created successfully";
    }

    // Get all users
    public List<User> getUsers() {
        return users;
    }

    public User getUserByEmail(String email) {
        for (User user: users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
}
