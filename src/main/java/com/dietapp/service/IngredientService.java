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
import com.dietapp.model.Food;
import com.dietapp.service.FoodService;
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
            if (parent != null && !parent.exists()) {
                // Ensure directory exists; if creation fails but directory still doesn't exist, we silently skip per your style
                if (!parent.mkdirs() && !parent.exists()) {
                    // Could log if you add logging later
                }
            }
            try (FileWriter fw = new FileWriter(storageFile)) {
                gson.toJson(ingredients, fw);
            }
        } catch (IOException e) {
            // ignore for now
        }
    }

    // ------------ Utility management methods ------------

    /** Clears all ingredients from memory and persists an empty list to disk. */
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
            if (equalsIgnoreCaseSafe(existing.getName(), name)) {
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
                    if (!other.getId().equals(id) && equalsIgnoreCaseSafe(other.getName(), name.trim())) {
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

    /**
     * Remove an ingredient by name (case-insensitive, trimmed).
     * Performs validation and checks whether any Food currently uses the ingredient.
     */
    public synchronized ValidationResult removeIngredientByName(String name) {
        if (name == null) return new ValidationResult(false, "Invalid ingredient name");
        String cleaned = name.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) return new ValidationResult(false, "Invalid ingredient name");

        int foundIdx = -1;
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient existing = ingredients.get(i);
            // (removed debug print) checking names
            if (equalsIgnoreCaseSafe(existing.getName(), cleaned)) {
                foundIdx = i;
                break;
            }
        }
        if (foundIdx == -1) return new ValidationResult(false, "Ingredient does not exist");

        // Check food dependencies: if any food uses this ingredient, refuse to delete
        try {
            FoodService fs = new FoodService();
            for (Food f : fs.getFoods()) {
                if (f.getIngredients() == null) continue;
                for (String ingName : f.getIngredients()) {
                    if (ingName != null && ingName.trim().equalsIgnoreCase(cleaned)) {
                        return new ValidationResult(false, "Ingredient is in use");
                    }
                }
            }
        } catch (Exception e) {
            // If food list cannot be read for some reason, surface a generic error
            return new ValidationResult(false, "Unable to verify ingredient usage");
        }

        // safe to delete
        ingredients.remove(foundIdx);
        persist();
        return new ValidationResult(true, "Ingredient removed");
    }

    // ------------ Simple filters (optional, analogous to FoodService) ------------

    /** Filters by dietary tags and allergens. */
    @SuppressWarnings("unused")
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
                            if (equalsIgnoreCaseSafe(it, t)) { has = true; break; }
                        }
                        if (!has) { ok = false; break; }
                    }
                }
            }

            if (ok && excludeAllergens != null && !excludeAllergens.isEmpty() && ing.getAllergens() != null) {
                for (String a : excludeAllergens) {
                    for (String ia : ing.getAllergens()) {
                        if (equalsIgnoreCaseSafe(ia, a)) { ok = false; break; }
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

    // ------------ helpers ------------
    private static boolean equalsIgnoreCaseSafe(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }
}
