package com.dietapp.model;

import lombok.Data;

@Data
public class User {

    private String id;
    private String name;
    private String email;
    private int age;
    private double weight;
    private boolean vegetarian;
    private boolean glutenFree;

    // No-args constructor
    public User() {}

    // All-args constructor
    public User(String id, String name, String email, int age, double weight, boolean vegetarian, boolean glutenFree) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.weight = weight;
        this.vegetarian = vegetarian;
        this.glutenFree = glutenFree;
    }
}
