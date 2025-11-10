package com.dietapp.spring.dto;

import com.dietapp.spring.entity.FoodEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class FoodDto {
    private String id;
    public FoodDto(FoodEntity entity) {
        if (entity == null) return;
        this.id = entity.getId();
        this.name = entity.getName();
        this.calories = entity.getCalories();
        this.servingSize = entity.getServingSize();
        this.quantity = entity.getQuantity();
        this.unit = entity.getUnit();
    }

    public FoodDto() {}

    @NotBlank
    private String name;

    @Positive
    private double calories;

    private String servingSize;
    private double quantity;
    private String unit;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
