package com.dietapp.spring.dto;

import lombok.Data;

@Data
public class AuthRequest {
    String name;
    String email;
    String password;
    int age;
    double weight;
    boolean vegetarian;
    boolean glutenFree;
}
