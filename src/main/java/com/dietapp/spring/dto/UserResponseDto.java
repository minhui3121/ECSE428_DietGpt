package com.dietapp.spring.dto;

import com.dietapp.model.User;

public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private int age;
    private double weight;
    private boolean vegetarian;
    private boolean glutenFree;
    private String password;

    @SuppressWarnings("unused")
    private UserResponseDto() {

    }

    public UserResponseDto(User model){
        this.id = model.getId();
        this.email = model.getEmail();
        this.age = model.getAge();
        this.weight = model.getWeight();
        this.name = model.getName();
        this.vegetarian = model.isVegetarian();
        this.glutenFree = model.isGlutenFree();
        this.password = model.getPassword();
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getAge(){
        return this.age;
    }

    public String getEmail(){
        return this.email;
    }

    public double getWeight(){
        return this.weight;
    }

    public boolean isVegetarian(){
        return this.vegetarian;
    }

    public boolean isGlutenFree(){
        return this.glutenFree;
    }

    public String getPassword(){
        return this.password;
    }
}

