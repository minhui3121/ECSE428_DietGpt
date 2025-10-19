package com.dietapp.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.dietapp.model.Food;

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
}
