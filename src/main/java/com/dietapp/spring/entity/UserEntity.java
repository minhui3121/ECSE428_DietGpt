package com.dietapp.spring.entity;

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
    
    public UserEntity() {}

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
} 
