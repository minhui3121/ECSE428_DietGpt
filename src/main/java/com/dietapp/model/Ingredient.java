package com.dietapp.model;

import java.util.List;

import lombok.Data;

@Data
public class Ingredient {
    private String id;
    private String name;
    private String unit;              // e.g., "g", "ml", "pcs"
    private double caloriesPerUnit;   // calories per unit (e.g., per 100g or per piece)
    private double quantity;          // quantity available or used
    private List<String> dietaryTags; // e.g., "vegan", "gluten-free"
    private List<String> allergens;   // e.g., "nuts", "soy"

    public Ingredient() {}

    public Ingredient(String id, String name, String unit, double caloriesPerUnit,
                      double quantity, List<String> dietaryTags, List<String> allergens) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.caloriesPerUnit = caloriesPerUnit;
        this.quantity = quantity;
        this.dietaryTags = dietaryTags;
        this.allergens = allergens;
    }
}
