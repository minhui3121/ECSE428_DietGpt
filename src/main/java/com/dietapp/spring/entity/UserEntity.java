package com.dietapp.spring.entity;

import java.util.List;

import com.dietapp.model.Food;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    private String name;
    private String email;
    private String password;
    private int age;
    private double weight;
    private boolean vegetarian;
    private boolean glutenFree;
    private List<FoodEntity> foodHistory;
    
    public UserEntity() {}

    public UserEntity(String name, String email, int age, String password, double weight, boolean vegetarian, boolean glutenFree){
        this.name = name;
        this.password = password;
        this.email = email;
        this.age = age;
        this.weight = weight;
        this.vegetarian = vegetarian;
        this.glutenFree = glutenFree;
        foodHistory = null;
    }

    //setters and getters for user
    public String getId(){
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName(){
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public boolean getVeg() {
        return vegetarian;
    }

    public boolean getGlut() {
        return glutenFree;
    }

    public double getWeight() {
        return weight;
    }

    public List<FoodEntity> getHist() {
        return foodHistory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVeg() {
        this.vegetarian = !vegetarian;
    }

    public void setGlut() {
        this.glutenFree = !glutenFree;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHist(List<FoodEntity> foodHistory) {
        this.foodHistory = foodHistory;
    }
} 
