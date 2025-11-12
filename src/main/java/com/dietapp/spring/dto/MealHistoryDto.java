package com.dietapp.spring.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MealHistoryDto {

    private Long mealId;
    private LocalDate date;
    private String mealName;
    private String ingredients;
    private Double calories;
    private String tags;

    public MealHistoryDto() {}

    public MealHistoryDto(Long mealId, LocalDate date, String mealName,
                          String ingredients, Double calories, String tags) {
        this.mealId = mealId;
        this.date = date;
        this.mealName = mealName;
        this.ingredients = ingredients;
        this.calories = calories;
        this.tags = tags;
    }

    //getters and setters
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
