package com.dietapp.service;

import java.util.ArrayList;
import java.util.List;

import com.dietapp.model.Meal;
import com.dietapp.model.User;
import com.dietapp.spring.dto.MealHistoryDto;

public class MealHistoryService {

    private List<Meal> meals = new ArrayList<>();

    public MealHistoryDto getMealById(User user, Long mealId) {
        if (user == null || user.getMealHistory() == null) {
            return null;
        }

        for (Meal meal : user.getMealHistory()) {
            if (meal.getMealId().equals(mealId)) {
                return new MealHistoryDto(
                        meal.getMealId(),
                        meal.getDate(),
                        meal.getMealName(),
                        meal.getIngredients(),
                        meal.getCalories(),
                        meal.getTags()
                );
            }
        }

        return null;
    }

    public List<MealHistoryDto> getAllMeals(User user) {
        List<MealHistoryDto> result = new ArrayList<>();

        if (user == null || user.getMealHistory() == null) {
            return result;
        }

        for (Meal meal : user.getMealHistory()) {
            result.add(new MealHistoryDto(
                    meal.getMealId(),
                    meal.getDate(),
                    meal.getMealName(),
                    meal.getIngredients(),
                    meal.getCalories(),
                    meal.getTags()
            ));
        }

        return result;
    }

    public String removeMeal(User user, Long mealId) {
        if (user == null || user.getMealHistory() == null) {
            return "User or meal history not found";
        }

        boolean removed = user.getMealHistory().removeIf(m -> m.getMealId().equals(mealId));

        if (removed) {
            return "Meal record removed successfully";
        } else {
            return "Meal record not found";
        }
    }
}
