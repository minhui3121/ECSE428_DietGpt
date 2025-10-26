package com.dietapp.steps;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.cucumber.datatable.DataTable;
import com.dietapp.model.Ingredient;
import com.dietapp.service.IngredientService;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

@SuppressWarnings("unused") // Cucumber loads these via reflection; IDE can't see direct usages
public class IngredientStepDefinitions {
    private final IngredientService ingredientService = new IngredientService();
    private int lastCode;
    private String lastBody;
    private final Map<String, String> lastFormData = new HashMap<>();

    @Before
    public void resetStatus() {
        lastCode = 0;
        lastBody = null;
        lastFormData.clear();
    }

    @Given("the ingredient store is empty")
    public void the_ingredient_store_is_empty() {
        ingredientService.clear();
    }

    @Given("an ingredient table with the following information:")
    public void ingredient_table_with_information(DataTable table) {
        lastFormData.clear();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String field = row.get("Field");
            String value = row.get("Value");
            if (field != null) lastFormData.put(field.trim(), value == null ? "" : value);
        }
    }

    @When("I add ingredient {string} with unit {string}")
    public void add_ingredient(String name, String unit) {
        Ingredient ing = new Ingredient();
        ing.setName(name);
        ing.setUnit(unit);
        IngredientService.ValidationResult vr = ingredientService.addIngredient(ing);
        if (vr.success) { lastCode = 201; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    @Given("I have added ingredient {string} with unit {string}")
    public void have_added_ingredient(String name, String unit) {
        add_ingredient(name, unit);
        assertTrue(lastCode == 201 || lastCode == 200, "Add ingredient did not succeed: " + lastBody);
    }

    @When("the user submits the add-ingredient form")
    public void submit_add_ingredient_form() {
        Ingredient ing = new Ingredient();

        String name = lastFormData.getOrDefault("Name", "");
        String unit = lastFormData.getOrDefault("Unit", null);
        String calStr = lastFormData.getOrDefault("CaloriesPerUnit", null);
        String qtyStr = lastFormData.getOrDefault("Quantity", null);
        String tagsStr = lastFormData.getOrDefault("DietaryTags", null);
        String allergensStr = lastFormData.getOrDefault("Allergens", null);

        ing.setName(name);
        ing.setUnit(unit);
        if (calStr != null && !calStr.isBlank()) {
            try { ing.setCaloriesPerUnit(Double.parseDouble(calStr.trim())); } catch (Exception ignore) { ing.setCaloriesPerUnit(0); }
        }
        if (qtyStr != null && !qtyStr.isBlank()) {
            try { ing.setQuantity(Double.parseDouble(qtyStr.trim())); } catch (Exception ignore) { ing.setQuantity(0); }
        }
        if (tagsStr != null && !tagsStr.isBlank()) {
            ing.setDietaryTags(splitCSV(tagsStr));
        }
        if (allergensStr != null && !allergensStr.isBlank()) {
            ing.setAllergens(splitCSV(allergensStr));
        }

        IngredientService.ValidationResult vr = ingredientService.addIngredient(ing);
        if (vr.success) { lastCode = 201; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    @When("the user submits the add-ingredient form again with the same Name")
    public void submit_add_ingredient_form_again() {
        submit_add_ingredient_form();
    }

    @When("the user updates the ingredient named {string} with the following information:")
    public void user_updates_ingredient_by_name(String existingName, DataTable table) {
        Map<String, String> update = new HashMap<>();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String field = row.get("Field");
            String value = row.get("Value");
            if (field != null) update.put(field.trim(), value == null ? "" : value);
        }

        List<Ingredient> list = ingredientService.getIngredients();
        Ingredient found = null;
        for (Ingredient i : list) { if (existingName.equals(i.getName())) { found = i; break; } }
        if (found == null) throw new RuntimeException("Ingredient not found: " + existingName);

        Ingredient updated = new Ingredient();
        if (update.containsKey("Name"))  updated.setName(update.get("Name"));
        if (update.containsKey("Unit"))  updated.setUnit(update.get("Unit"));

        if (update.containsKey("CaloriesPerUnit")) {
            try { updated.setCaloriesPerUnit(Double.parseDouble(update.get("CaloriesPerUnit").trim())); }
            catch (Exception e) { updated.setCaloriesPerUnit(0); }
        }
        if (update.containsKey("Quantity")) {
            try { updated.setQuantity(Double.parseDouble(update.get("Quantity").trim())); }
            catch (Exception e) { updated.setQuantity(0); }
        }
        if (update.containsKey("DietaryTags")) {
            updated.setDietaryTags(splitCSV(update.get("DietaryTags")));
        }
        if (update.containsKey("Allergens")) {
            updated.setAllergens(splitCSV(update.get("Allergens")));
        }

        IngredientService.ValidationResult vr = ingredientService.updateIngredient(found.getId(), updated);
        if (vr.success) { lastCode = 200; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    @When("I change the ingredient named {string} to have name {string} and unit {string}")
    public void change_ingredient_by_name(String currentName, String newName, String newUnit) {
        List<Ingredient> list = ingredientService.getIngredients();
        Ingredient found = null;
        for (Ingredient i : list) { if (currentName.equals(i.getName())) { found = i; break; } }
        if (found == null) throw new RuntimeException("Ingredient not found: " + currentName);

        Ingredient updated = new Ingredient();
        updated.setName(newName);
        updated.setUnit(newUnit);

        IngredientService.ValidationResult vr = ingredientService.updateIngredient(found.getId(), updated);
        if (vr.success) { lastCode = 200; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    @Then("the API should return success for ingredients")
    public void api_success_for_ingredients() {
        assertTrue(lastCode == 201 || lastCode == 200, "Expected success but was " + lastCode + " body:" + lastBody);
    }

    @Then("the API should return an ingredient error containing {string}")
    public void api_error_contains_for_ingredients(String fragment) {
        assertTrue(lastCode >= 400 && lastCode < 500, "Expected 4xx but was " + lastCode + " body:" + lastBody);
        if (lastBody != null) {
            assertTrue(lastBody.toLowerCase().contains(fragment.toLowerCase()),
                    "Expected response body to contain: " + fragment + " but was: " + lastBody);
        }
    }

    @Then("the system should contain an ingredient named {string} with unit {string}")
    public void system_should_contain(String name, String unit) {
        List<Ingredient> list = ingredientService.getIngredients();
        boolean found = false;
        if (list != null) {
            for (Ingredient i : list) {
                if (name.equals(i.getName()) &&
                        ((unit == null && (i.getUnit() == null || i.getUnit().isEmpty())) ||
                                (unit != null && unit.equals(i.getUnit())))) {
                    found = true; break;
                }
            }
        }
        assertTrue(found, "Ingredient not found in list: name=" + name + " unit=" + unit);
    }

    // Alias that matches your feature step verbatim
    @Then("I should see an error {string}")
    public void i_should_see_an_error(String err) {
        assertTrue(lastCode >= 400 && lastCode < 500, "Expected 4xx but was " + lastCode + " body:" + lastBody);
        assertNotNull(lastBody, "Missing error body");
        assertTrue(lastBody.toLowerCase().contains(err.toLowerCase()),
                "Expected error to contain: " + err + " but was: " + lastBody);
    }

    @Then("I should see an ingredient error {string}")
    public void should_see_ingredient_error(String err) {
        i_should_see_an_error(err);
    }

    // ---------- helpers ----------
    private static List<String> splitCSV(String s) {
        if (s == null || s.isBlank()) return new ArrayList<>();
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }
}
