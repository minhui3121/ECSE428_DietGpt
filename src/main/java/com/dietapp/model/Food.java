package com.dietapp.model;

import java.util.List;

import lombok.Data;

@Data
public class Food {
    private String id;
    private String name;
    private double calories; // per serving
    private String servingSize; // e.g., "100g"
    private List<String> ingredients;
    private List<String> dietaryTags; // e.g., vegetarian, low-carb
    private List<String> allergens; // e.g., peanuts
    private double quantity; // amount available
    private String unit; // e.g., g, ml, pcs

    public Food() {}

    public Food(String id, String name, double calories, String servingSize, java.util.List<String> ingredients,
                java.util.List<String> dietaryTags, java.util.List<String> allergens, double quantity, String unit) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.servingSize = servingSize;
        this.ingredients = ingredients;
        this.dietaryTags = dietaryTags;
        this.allergens = allergens;
        this.quantity = quantity;
        this.unit = unit;
    }
}
