package com.dietapp.spring.dto;

import java.util.List;

public class MealHistoryResponse {

    private Long userId;
    private List<MealHistoryDto> meals;

    public MealHistoryResponse() {}

    public MealHistoryResponse(Long userId, List<MealHistoryDto> meals) {
        this.userId = userId;
        this.meals = meals;
    }

    //getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<MealHistoryDto> getMeals() { return meals; }
    public void setMeals(List<MealHistoryDto> meals) { this.meals = meals; }
}
