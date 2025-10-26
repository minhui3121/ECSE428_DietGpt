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

import com.dietapp.model.Ingredient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class IngredientService {
    private final List<Ingredient> ingredients;
    private final File storageFile;
    private final Gson gson = new Gson();

    // Allowed and normalized units expected by the service
    private static final Set<String> ALLOWED_UNITS = Set.of(
            "GRAM", "G", "KILOGRAM", "KG",
            "MILLILITER", "ML", "LITER", "L",
            "PCS", "PIECE", "UNIT"
    );

    public IngredientService() {
        this.storageFile = new File("data/ingredients.json");
        this.ingredients = Collections.synchronizedList(new ArrayList<>());
        load();
    }

    private void load() {
        if (storageFile.exists()) {
            try (FileReader fr = new FileReader(storageFile)) {
                List<Ingredient> list = gson.fromJson(fr, new TypeToken<List<Ingredient>>(){}.getType());
                if (list != null) {
                    ingredients.addAll(list);
                }
            } catch (IOException e) {
                // ignore - start empty
            }
        }
    }

    private void persist() {
        try {
            File parent = storageFile.getParentFile();
            if (parent != null) parent.mkdirs();
            try (FileWriter fw = new FileWriter(storageFile)) {
                gson.toJson(ingredients, fw);
            }
        } catch (IOException e) {
            // ignore for now
        }
    }

    // ------------ Utility management methods ------------

    /**
     * Clears all ingredients from memory and persists an empty list to disk.
     * Used in tests or setup steps to start from a clean state.
     */
    public synchronized void clear() {
        ingredients.clear();
        persist();
    }

    // ------------ CRUD ------------

    public synchronized ValidationResult addIngredient(Ingredient ing) {
        if (ing == null) return new ValidationResult(false, "Ingredient is null");

        String name = ing.getName() == null ? "" : ing.getName().trim();
        if (name.isEmpty()) return new ValidationResult(false, "Name is required");
        if (name.length() > 200) return new ValidationResult(false, "Name too long");

        // unit optional; if present, normalize and validate
        if (ing.getUnit() != null) {
            String unit = ing.getUnit().trim();
            if (!unit.isEmpty()) {
                String u = unit.toUpperCase();
                if (!ALLOWED_UNITS.contains(u)) {
                    return new ValidationResult(false, "Invalid unit");
                }
                ing.setUnit(u);
            } else {
                ing.setUnit("");
            }
        }

        // calories/quantity must be non-negative (0 allowed)
        if (ing.getCaloriesPerUnit() < 0) return new ValidationResult(false, "Calories per unit must be non-negative");
        if (ing.getQuantity() < 0) return new ValidationResult(false, "Quantity must be non-negative");

        // duplicate by name (case-insensitive)
        for (Ingredient existing : ingredients) {
            if (existing.getName() != null && existing.getName().equalsIgnoreCase(name)) {
                // Match feature expectation exactly:
                return new ValidationResult(false, "Ingredient already exists");
            }
        }

        ing.setId(UUID.randomUUID().toString());
        ingredients.add(ing);
        persist();
        return new ValidationResult(true, "Ingredient added");
    }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public synchronized ValidationResult updateIngredient(String id, Ingredient ing) {
        if (id == null || id.isEmpty()) return new ValidationResult(false, "Invalid id");
        if (ing == null) return new ValidationResult(false, "Ingredient is null");

        String name = ing.getName();
        if (name == null || name.trim().isEmpty()) return new ValidationResult(false, "Name is required");
        if (name.trim().length() > 200) return new ValidationResult(false, "Name too long");

        // Normalize/validate unit if provided
        if (ing.getUnit() != null) {
            String unit = ing.getUnit().trim();
            if (!unit.isEmpty()) {
                String u = unit.toUpperCase();
                if (!ALLOWED_UNITS.contains(u)) {
                    return new ValidationResult(false, "Invalid unit");
                }
                ing.setUnit(u);
            } else {
                ing.setUnit("");
            }
        }

        if (ing.getCaloriesPerUnit() < 0) return new ValidationResult(false, "Calories per unit must be non-negative");
        if (ing.getQuantity() < 0) return new ValidationResult(false, "Quantity must be non-negative");

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient existing = ingredients.get(i);
            if (existing.getId().equals(id)) {
                // duplicate name with other items
                for (Ingredient other : ingredients) {
                    if (!other.getId().equals(id) &&
                            other.getName() != null &&
                            other.getName().equalsIgnoreCase(name.trim())) {
                        // Match feature expectation exactly:
                        return new ValidationResult(false, "Ingredient already exists");
                    }
                }

                // update fields
                existing.setName(name.trim());
                existing.setUnit(ing.getUnit());
                existing.setCaloriesPerUnit(ing.getCaloriesPerUnit());
                existing.setQuantity(ing.getQuantity());
                existing.setDietaryTags(ing.getDietaryTags());
                existing.setAllergens(ing.getAllergens());

                persist();
                return new ValidationResult(true, "Ingredient updated");
            }
        }
        return new ValidationResult(false, "Ingredient not found");
    }

    public synchronized boolean deleteIngredient(String id) {
        if (id == null) return false;
        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i).getId().equals(id)) {
                ingredients.remove(i);
                persist();
                return true;
            }
        }
        return false;
    }

    // ------------ Simple filters (optional, analogous to FoodService) ------------

    /**
     * Filters by dietary tags and allergens. If both lists are empty or null, returns all.
     * includeTags: every tag in includeTags must be present (case-insensitive)
     * excludeAllergens: excludes any ingredient that has at least one of these allergens (case-insensitive)
     */
    public synchronized List<Ingredient> filterIngredients(List<String> includeTags, List<String> excludeAllergens) {
        List<Ingredient> out = new ArrayList<>();
        for (Ingredient ing : ingredients) {
            boolean ok = true;

            if (includeTags != null && !includeTags.isEmpty()) {
                if (ing.getDietaryTags() == null) {
                    ok = false;
                } else {
                    for (String t : includeTags) {
                        boolean has = false;
                        for (String it : ing.getDietaryTags()) {
                            if (it != null && t != null && it.equalsIgnoreCase(t)) { has = true; break; }
                        }
                        if (!has) { ok = false; break; }
                    }
                }
            }

            if (ok && excludeAllergens != null && !excludeAllergens.isEmpty() && ing.getAllergens() != null) {
                for (String a : excludeAllergens) {
                    for (String ia : ing.getAllergens()) {
                        if (ia != null && a != null && ia.equalsIgnoreCase(a)) { ok = false; break; }
                    }
                    if (!ok) break;
                }
            }

            if (ok) out.add(ing);
        }
        return out;
    }

    // ------------ Result wrapper ------------

    public static class ValidationResult {
        public final boolean success;
        public final String message;

        public ValidationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
