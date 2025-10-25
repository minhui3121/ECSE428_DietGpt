package com.dietapp.spring.service;

import com.dietapp.model.User;
import com.dietapp.spring.dto.UserResponseDto;
import com.dietapp.spring.entity.UserEntity;
import com.dietapp.spring.repo.UserRepository;

import jakarta.transaction.Transactional;

public class UserService {
    private UserRepository userRepo;

    @Transactional
    public UserEntity findUserById(String userId){
        UserEntity user = userRepo.findUserById(userId);

        if (user == null){
            throw new IllegalArgumentException("No user found with id " + userId);
        }

        return user;
    }

    @Transactional
    public UserEntity createUser(String name, String email, String password, int age, double weight, boolean vegetarian, boolean glutenFree) {
        // Validate required fields
        UserEntity user = new UserEntity(name, email, age, password, weight, vegetarian, glutenFree);
        if(user.getName() == null || user.getName().isEmpty() ||
           user.getEmail() == null || user.getEmail().isEmpty() ||
           user.getAge() <= 0 || user.getWeight() <= 0) {
            throw new IllegalArgumentException( "Please fill in all required fields");
        }

        // Validate email format
        if(!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check for duplicate email
        if (userRepo.findUserByEmail(email) != null){
            throw new IllegalArgumentException("Email address already in use");
        }

        return userRepo.save(user);
    }

    public UserEntity updateUser(String id, UserResponseDto dto){
        if (userRepo.findUserById(id) == null) {
            throw new IllegalArgumentException("No user is linked to this user ID");
        }
        UserEntity user = userRepo.findUserById(id);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setWeight(dto.getWeight());
        if (user.getGlut() != dto.isGlutenFree()){
            user.setGlut();
        }
        if (user.getVeg() != dto.isVegetarian()){
            user.setVeg();
        }
        userRepo.save(user);
        return user;
    }
    
}