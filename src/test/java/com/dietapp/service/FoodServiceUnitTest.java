package com.dietapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import java.nio.file.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dietapp.model.Food;
import com.dietapp.model.Ingredient;

public class FoodServiceUnitTest {

    private static final Path DIRECTORY_PATH = Paths.get("data");

    @BeforeEach
    void cleanDirectory() throws IOException {
        if (Files.exists(DIRECTORY_PATH)) {
            deleteRecursively(DIRECTORY_PATH);
        }
        Files.createDirectories(DIRECTORY_PATH); // optional: recreate the empty folder
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var entries = Files.list(path)) {
                for (Path entry : entries.toList()) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }
    
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
        a1.setCalories(1);
        b1.setCalories(1);
        c1.setCalories(1);
        d1.setCalories(1);
        svc.addFood(a1);
        svc.addFood(b1);
        svc.addFood(c1);
        svc.addFood(d1);

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
        a1.setCalories(1);
        b1.setCalories(1);
        c1.setCalories(1);
        d1.setCalories(1);
        svc.addFood(a1);
        svc.addFood(b1);
        svc.addFood(c1);
        svc.addFood(d1);

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
        a1.setCalories(1);
        b1.setCalories(1);
        c1.setCalories(1);
        d1.setCalories(1);
        svc.addFood(a1);
        svc.addFood(b1);
        svc.addFood(c1);
        svc.addFood(d1);

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
        a1.setCalories(1);
        b1.setCalories(1);
        c1.setCalories(1);
        d1.setCalories(1);
        svc.addFood(a1);
        svc.addFood(b1);
        svc.addFood(c1);
        svc.addFood(d1);

        //Act
        List<Food> result = svc.filterFoodsByDietaryPreference(false, false);

        //Assert
        assertEquals(4, result.size());
    }


}
