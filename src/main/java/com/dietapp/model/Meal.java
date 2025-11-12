package com.dietapp.model;

import java.time.LocalDate;
import java.util.List;

public class Meal {

    private String mealId;
    private String userId;
    private LocalDate date;
    private String mealName;
    private List<String> ingredients;
    private int calories;
    private List<String> tags;

    public Meal() {}

    public Meal(String mealId, String userId, LocalDate date,
                String mealName, List<String> ingredients,
                int calories, List<String> tags) {
        this.mealId = mealId;
        this.userId = userId;
        this.date = date;
        this.mealName = mealName;
        this.ingredients = ingredients;
        this.calories = calories;
        this.tags = tags;
    }

    //getters and setters
    public String getMealId() { return mealId; }
    public void setMealId(String mealId) { this.mealId = mealId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    @Override
    public String toString() {
        return "Meal{" +
                "mealId='" + mealId + '\'' +
                ", userId='" + userId + '\'' +
                ", date=" + date +
                ", mealName='" + mealName + '\'' +
                ", ingredients=" + ingredients +
                ", calories=" + calories +
                ", tags=" + tags +
                '}';
    }
}
