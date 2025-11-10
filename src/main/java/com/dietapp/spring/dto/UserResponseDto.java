package com.dietapp.spring.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.dietapp.model.User;
import com.dietapp.spring.entity.UserEntity;

public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private int age;
    private double weight;
    private boolean vegetarian;
    private boolean glutenFree;
    private String password;
    private List<FoodDto> foodHistory;

    @SuppressWarnings("unused")
    private UserResponseDto() {

    }

    public UserResponseDto(UserEntity model){
        this.id = model.getId();
        this.email = model.getEmail();
        this.age = model.getAge();
        this.weight = model.getWeight();
        this.name = model.getName();
        this.vegetarian = model.getVeg();
        this.glutenFree = model.getGlut();
        this.password = model.getPassword();
        if (model.getHist() != null) {
            this.foodHistory = model.getHist().stream()
                .map(FoodDto::new)
                .collect(Collectors.toList());}
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

    public List<FoodDto> getHist(){
        return this.foodHistory;
    }
}

