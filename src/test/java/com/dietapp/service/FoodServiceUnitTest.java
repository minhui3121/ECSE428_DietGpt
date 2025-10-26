package com.dietapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dietapp.model.Food;
import com.dietapp.model.Ingredient;

public class FoodServiceUnitTest {
    
    @Test
    public void addValidFood() {
        // ensure no persisted data from other tests
        java.nio.file.Path p = java.nio.file.Paths.get("data/foods.json");
        try { java.nio.file.Files.deleteIfExists(p); } catch (Exception e) {}
        FoodService svc = new FoodService();
        Food f = new Food();
        f.setName("Apple");
        f.setCalories(52.0);
        FoodService.ValidationResult vr = svc.addFood(f);
        assertTrue(vr.success, "valid food should be accepted");
    }

    @Test
    public void rejectZeroCalories() {
        java.nio.file.Path p = java.nio.file.Paths.get("data/foods.json");
        try { java.nio.file.Files.deleteIfExists(p); } catch (Exception e) {}
        FoodService svc = new FoodService();
        Food f = new Food();
        f.setName("Water");
        f.setCalories(0.0);
        FoodService.ValidationResult vr = svc.addFood(f);
        assertFalse(vr.success, "zero calories should be rejected");
    }

    @Test
    public void testFilterGlutenFree() {
        //Assert
        FoodService svc = new FoodService();
        Ingredient a = new Ingredient();
        Food a1 = new Food();
        Food b1 = new Food();
        Food c1 = new Food();
        Food d1 = new Food();
        a1.setName("Pasta");
        b1.setName("Salad");
        c1.setName("Chicken Sandwich");
        d1.setName("Dumplings");
        a1.setDietaryTags(List.of("gluten-free"));
        b1.setDietaryTags(List.of("vegan", "vegetarian"));
        d1.setDietaryTags(List.of("vegan"));
        b1.setAllergens(List.of("nuts", "soy"));

        //Act
        List<Food> result = svc.filterFoodsByDietaryPreference(false, true);

        //Assert
        assertEquals(1, result.size());
    }

    @Test
    public void testFilterVegetarian() {
        //Assert
        FoodService svc = new FoodService();
        Ingredient a = new Ingredient();
        Food a1 = new Food();
        Food b1 = new Food();
        Food c1 = new Food();
        Food d1 = new Food();
        a1.setName("Pasta");
        b1.setName("Salad");
        c1.setName("Chicken Sandwich");
        d1.setName("Dumplings");
        a1.setDietaryTags(List.of("gluten-free"));
        b1.setDietaryTags(List.of("vegan", "vegetarian"));
        d1.setDietaryTags(List.of("vegan"));
        b1.setAllergens(List.of("nuts", "soy"));

        //Act
        List<Food> result = svc.filterFoodsByDietaryPreference(true, false);

        //Assert
        assertEquals(1, result.size());
    }

    @Test
    public void testFilterGlutenFreeAndVegetarian() {
        //Assert
        FoodService svc = new FoodService();
        Ingredient a = new Ingredient();
        Food a1 = new Food();
        Food b1 = new Food();
        Food c1 = new Food();
        Food d1 = new Food();
        a1.setName("Pasta");
        b1.setName("Salad");
        c1.setName("Chicken Sandwich");
        d1.setName("Dumplings");
        a1.setDietaryTags(List.of("gluten-free"));
        b1.setDietaryTags(List.of("gluten-free", "vegetarian"));
        d1.setDietaryTags(List.of("vegetarian"));
        b1.setAllergens(List.of("nuts", "soy"));

        //Act
        List<Food> result = svc.filterFoodsByDietaryPreference(true, true);

        //Assert
        assertEquals(1, result.size());
    }

    @Test
    public void testNoPreference() {
        //Assert
        FoodService svc = new FoodService();
        Food a1 = new Food();
        Food b1 = new Food();
        Food c1 = new Food();
        Food d1 = new Food();
        a1.setName("Pasta");
        b1.setName("Salad");
        c1.setName("Chicken Sandwich");
        d1.setName("Dumplings");
        a1.setDietaryTags(List.of("gluten-free"));
        b1.setDietaryTags(List.of("vegan", "vegetarian"));
        c1.setDietaryTags(List.of("vegan"));
        d1.setDietaryTags(List.of("vegan"));
        b1.setAllergens(List.of("nuts", "soy"));

        //Act
        List<Food> result = svc.filterFoodsByDietaryPreference(false, false);

        //Assert
        assertEquals(4, result.size());
    }


}
