package com.dietapp.spring.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "meal_history")
public class MealEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mealId;

    private Long userId;
    private LocalDate date;
    private String mealName;
    private String ingredients;
    private Double calories;
    private String tags;

    //getters and Setters
    public Long getMealId() { return mealId; }
    public void setMealId(Long mealId) { this.mealId = mealId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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
