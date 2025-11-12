package com.dietapp.model;

import java.time.LocalDate;

public class Meal {
    private Long mealId;
    private LocalDate date;
    private String mealName;
    private String ingredients;
    private Double calories;
    private String tags;

    public Meal() {}

    public Meal(Long mealId, LocalDate date, String mealName,
                String ingredients, Double calories, String tags) {
        this.mealId = mealId;
        this.date = date;
        this.mealName = mealName;
        this.ingredients = ingredients;
        this.calories = calories;
        this.tags = tags;
    }

    //getters and Setters
    public Long getMealId() { return mealId; }
    public void setMealId(Long mealId) { this.mealId = mealId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
