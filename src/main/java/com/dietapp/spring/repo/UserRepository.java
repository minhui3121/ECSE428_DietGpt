package com.dietapp.spring.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dietapp.spring.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String>{
    public UserEntity findUserById(String id);
    public UserEntity findUserByEmail(String email);
} 
