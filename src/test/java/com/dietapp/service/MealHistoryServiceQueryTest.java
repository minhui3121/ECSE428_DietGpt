package com.dietapp.service;

import com.dietapp.model.Meal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MealHistoryServiceQueryTest {

    private MealHistoryService service;

    @BeforeEach
    void setUp() {
        service = new MealHistoryService();

        service.loadMealsForUser("101", List.of(
                createMeal(
                        "M-2025-11-01-001",
                        "101",
                        LocalDate.of(2025, 11, 1),
                        "Chicken Rice",
                        List.of("Chicken", "Rice"),
                        620,
                        List.of("Non-Vegetarian")
                ),
                createMeal(
                        "M-2025-11-02-001",
                        "101",
                        LocalDate.of(2025, 11, 2),
                        "Vegetable Stir-Fry",
                        List.of("Broccoli", "Rice", "Carrot"),
                        420,
                        List.of("Vegetarian", "Gluten-Free")
                ),
                createMeal(
                        "M-2025-11-03-001",
                        "101",
                        LocalDate.of(2025, 11, 3),
                        "Oatmeal & Berries",
                        List.of("Oats", "Milk", "Berries"),
                        350,
                        List.of("Vegetarian")
                )
        ));

        // Bob -> userId "102"
        service.loadMealsForUser("102", List.of(
                createMeal(
                        "M-2025-11-01-101",
                        "102",
                        LocalDate.of(2025, 11, 1),
                        "Pancakes",
                        List.of("Eggs", "Milk", "Flour"),
                        500,
                        List.of("Vegetarian")
                ),
                createMeal(
                        "M-2025-11-02-101",
                        "102",
                        LocalDate.of(2025, 11, 2),
                        "Omelette",
                        List.of("Eggs", "Cheese", "Spinach"),
                        430,
                        List.of("Vegetarian")
                )
        ));
    }

    private Meal createMeal(String mealId,
                            String userId,
                            LocalDate date,
                            String mealName,
                            List<String> ingredients,
                            int calories,
                            List<String> tags) {
        Meal m = new Meal();
        m.setMealId(mealId);
        m.setUserId(userId);
        m.setDate(date);
        m.setMealName(mealName);
        m.setIngredients(ingredients);
        m.setCalories(calories);
        m.setTags(tags);
        return m;
    }

    @Test
    void queryMealsByDateRange_normalFlow() {
        List<Meal> result = service.queryMealsByDateRange(
                "101",
                LocalDate.of(2025, 11, 1),
                LocalDate.of(2025, 11, 2)
        );

        assertEquals(2, result.size());
        assertEquals("Chicken Rice", result.get(0).getMealName());
        assertEquals(LocalDate.of(2025, 11, 1), result.get(0).getDate());
        assertEquals("Vegetable Stir-Fry", result.get(1).getMealName());
        assertEquals(LocalDate.of(2025, 11, 2), result.get(1).getDate());
    }

    @Test
    void getMostRecentMeal_normalFlow() {
        Optional<Meal> recent = service.getMostRecentMeal("101");

        assertTrue(recent.isPresent());
        Meal meal = recent.get();
        assertEquals("Oatmeal & Berries", meal.getMealName());
        assertEquals(LocalDate.of(2025, 11, 3), meal.getDate());
        assertEquals(350, meal.getCalories());
    }

    @Test
    void getMealById_normalFlow() {
        Optional<Meal> mealOpt = service.getMealById("101", "M-2025-11-02-001");

        assertTrue(mealOpt.isPresent());
        Meal meal = mealOpt.get();

        assertEquals("M-2025-11-02-001", meal.getMealId());
        assertEquals("Vegetable Stir-Fry", meal.getMealName());
        assertEquals(List.of("Broccoli", "Rice", "Carrot"), meal.getIngredients());
        assertEquals(420, meal.getCalories());
        assertEquals(List.of("Vegetarian", "Gluten-Free"), meal.getTags());
    }



    @Test
    void queryMealsByDateRangeAndTag_filterVegetarian() {
        List<Meal> result = service.queryMealsByDateRangeAndTag(
                "101",
                LocalDate.of(2025, 11, 1),
                LocalDate.of(2025, 11, 3),
                "Vegetarian"
        );

        assertEquals(2, result.size());
        assertEquals("Vegetable Stir-Fry", result.get(0).getMealName());
        assertEquals(LocalDate.of(2025, 11, 2), result.get(0).getDate());
        assertEquals("Oatmeal & Berries", result.get(1).getMealName());
        assertEquals(LocalDate.of(2025, 11, 3), result.get(1).getDate());

        assertTrue(result.stream().noneMatch(m -> m.getMealName().equals("Chicken Rice")));
    }


    @Test
    void queryMealsByDateRange_noMealsFound() {
        List<Meal> result = service.queryMealsByDateRange(
                "101",
                LocalDate.of(2025, 10, 1),
                LocalDate.of(2025, 10, 7)
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMealById_mealNotFound() {
        Optional<Meal> mealOpt = service.getMealById("101", "M-9999-01-01-XYZ");

        assertTrue(mealOpt.isEmpty());
    }

    @Test
    void queryMealsByDateRange_invalidRange() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.queryMealsByDateRange(
                        "101",
                        LocalDate.of(2025, 11, 10),
                        LocalDate.of(2025, 11, 1)
                )
        );

        assertEquals("Invalid date range", ex.getMessage());
    }
}
