package com.dietapp.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.dietapp.model.Food;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FoodService {
    private final List<Food> foods;
    private final File storageFile;
    private final Gson gson = new Gson();

    public FoodService() {
        this.storageFile = new File("data/foods.json");
        this.foods = Collections.synchronizedList(new ArrayList<>());
        load();
    }

    private void load() {
        if (storageFile.exists()) {
            try (FileReader fr = new FileReader(storageFile)) {
                List<Food> list = gson.fromJson(fr, new TypeToken<List<Food>>(){}.getType());
                if (list != null) {
                    foods.addAll(list);
                }
            } catch (IOException e) {
                // ignore - start empty
            }
        }
    }

    private void persist() {
        try {
            storageFile.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(storageFile)) {
                gson.toJson(foods, fw);
            }
        } catch (IOException e) {
            // ignore for now
        }
    }

    public synchronized ValidationResult addFood(Food f) {
        if (f == null) return new ValidationResult(false, "Food is null");
        String name = f.getName() == null ? "" : f.getName().trim();
        if (name.isEmpty()) return new ValidationResult(false, "Name is required");
        if (name.length() > 200) return new ValidationResult(false, "Name too long");
        if (f.getCalories() <= 0) return new ValidationResult(false, "Calories must be positive");
        // duplicate by name
        for (Food existing : foods) {
            if (existing.getName().equalsIgnoreCase(f.getName().trim())) {
                return new ValidationResult(false, "Food with this name already exists");
            }
        }
        f.setId(UUID.randomUUID().toString());
        foods.add(f);
        persist();
        return new ValidationResult(true, "Food added");
    }

    public List<Food> getFoods() {
        return new ArrayList<>(foods);
    }

    public synchronized ValidationResult updateFood(String id, Food f) {
        if (id == null || id.isEmpty()) return new ValidationResult(false, "Invalid id");
        if (f == null) return new ValidationResult(false, "Food is null");
        if (f.getName() == null || f.getName().trim().isEmpty()) return new ValidationResult(false, "Name is required");
        if (f.getCalories() <= 0) return new ValidationResult(false, "Calories must be positive");

        for (int i = 0; i < foods.size(); i++) {
            Food existing = foods.get(i);
            if (existing.getId().equals(id)) {
                // check duplicate name with other items
                for (Food other : foods) {
                    if (!other.getId().equals(id) && other.getName().equalsIgnoreCase(f.getName().trim())) {
                        return new ValidationResult(false, "Food with this name already exists");
                    }
                }
                // update fields
                existing.setName(f.getName());
                existing.setCalories(f.getCalories());
                existing.setServingSize(f.getServingSize());
                existing.setIngredients(f.getIngredients());
                existing.setDietaryTags(f.getDietaryTags());
                existing.setAllergens(f.getAllergens());
                existing.setQuantity(f.getQuantity());
                existing.setUnit(f.getUnit());
                persist();
                return new ValidationResult(true, "Food updated");
            }
        }
        return new ValidationResult(false, "Food not found");
    }

    public synchronized List<Food> filterFoodsByDietaryPreference(boolean isVegetarian, boolean isGlutenFree) {
        List<Food> filteredFoods = new ArrayList<>();

        for (int i = 0; i < foods.size(); i++) {
            boolean hasPreferences = true;

            List<String> dietaryTags = foods.get(i).getDietaryTags();

            if (dietaryTags == null && (isVegetarian || isGlutenFree)) {
                continue;
            }

            if (isVegetarian) {
                boolean hasVegetarian = false;
                for (int j = 0; j < dietaryTags.size(); j ++) {
                    if (dietaryTags.get(j).equalsIgnoreCase("vegetarian")) {
                        hasVegetarian = true;
                        break;
                    }
                }
                if (!hasVegetarian) {
                    hasPreferences = false;
                }
            }

            if (isGlutenFree && hasPreferences) {
                boolean hasGF = false;
                for (int j = 0; j < dietaryTags.size(); j ++) {
                    if (dietaryTags.get(j).equalsIgnoreCase("gluten-free")) {
                        hasGF = true;
                        break;
                    }
                }
                if (!hasGF) {
                    hasPreferences = false;
                }
            }

            if (hasPreferences) {
                filteredFoods.add(foods.get(i));
            }
        }

        return filteredFoods;
    }

    public synchronized boolean deleteFood(String id) {
        if (id == null) return false;
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).getId().equals(id)) {
                foods.remove(i);
                persist();
                return true;
            }
        }
        return false;
    }

    public static class ValidationResult {
        public final boolean success;
        public final String message;

        public ValidationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public List<Food> findFoodsByAvailabilityAndPreferences(
            Set<String> availableIngredientNames,
            boolean isVegetarian,
            boolean isGlutenFree
    ) {
        // 1) Filter foods by dietary preference
        List<Food> prefFiltered = filterFoodsByDietaryPreference(isVegetarian, isGlutenFree);

        // 2) Normalize available ingredient names
        Set<String> normalizedAvail = availableIngredientNames.stream()
                .map(s -> s == null ? "" : s.trim().toLowerCase())
                .collect(java.util.stream.Collectors.toSet());

        // 3) Keep only foods whose ingredients are all available
        List<Food> result = new ArrayList<>();
        for (Food food : prefFiltered) {
            List<String> ing = food.getIngredients();
            if (ing == null || ing.isEmpty()) continue;
            boolean allAvailable = ing.stream()
                    .map(s -> s == null ? "" : s.trim().toLowerCase())
                    .allMatch(normalizedAvail::contains);
            if (allAvailable) {
                result.add(food);
            }
        }
        return result;
    }
    }

