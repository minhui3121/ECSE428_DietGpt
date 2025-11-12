package com.dietapp.service;

import com.dietapp.model.Meal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MealHistoryService {

    private Map<String, List<Meal>> userMeals = new HashMap<>();

    public void loadMealsForUser(String userId, List<Meal> meals) {
        userMeals.put(userId, new ArrayList<>(meals));
    }

    public List<Meal> queryMealsByDateRange(String userId, LocalDate start, LocalDate end) {
        if (start.isAfter(end)) throw new IllegalArgumentException("Invalid date range");
        return userMeals.getOrDefault(userId, List.of()).stream()
                .filter(m -> !m.getDate().isBefore(start) && !m.getDate().isAfter(end))
                .sorted(Comparator.comparing(Meal::getDate))
                .collect(Collectors.toList());
    }

    public List<Meal> queryMealsByDateRangeAndTag(String userId, LocalDate start, LocalDate end, String tag) {
        return queryMealsByDateRange(userId, start, end).stream()
                .filter(m -> m.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(tag)))
                .collect(Collectors.toList());
    }

    public Optional<Meal> getMostRecentMeal(String userId) {
        return userMeals.getOrDefault(userId, List.of()).stream()
                .max(Comparator.comparing(Meal::getDate));
    }
    
    public Optional<Meal> getMealById(String userId, String mealId) {
        return userMeals.getOrDefault(userId, List.of()).stream()
                .filter(m -> m.getMealId().equals(mealId))
                .findFirst();
    }
}
