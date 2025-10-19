package com.dietapp.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.cucumber.datatable.DataTable;
import com.google.gson.Gson;
import com.dietapp.model.Food;
import com.dietapp.service.FoodService;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class FoodStepDefinitions {
    private FoodService foodService;
    private int lastCode;
    private String lastBody;
    private final Gson gson = new Gson();
    private Map<String, String> lastFormData = new HashMap<>();

    @Before
    public void before() throws Exception {}

    @Given("the server is running")
    public void server_running() throws Exception {
        // Use the service layer directly in tests (no embedded server). Ensure fresh storage for each scenario.
        java.nio.file.Path p = java.nio.file.Paths.get("data/foods.json");
        try { java.nio.file.Files.deleteIfExists(p); } catch (Exception e) {}
        foodService = new FoodService();
    }

    @Given("a user provides the following information:")
    public void user_provides_information(DataTable table) {
        lastFormData.clear();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        // Expect one row; but handle multiple by merging
        for (Map<String, String> row : rows) {
            String field = row.get("Field");
            String value = row.get("Value");
            if (field != null) lastFormData.put(field.trim(), value == null ? "" : value);
        }
    }

    @When("I add a food with name \"{string}\" and calories {int}")
    public void add_food(String name, int calories) throws Exception {
        Food f = new Food();
        f.setName(name);
        f.setCalories(calories);
        FoodService.ValidationResult vr = foodService.addFood(f);
        if (vr.success) { lastCode = 201; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    @Then("the API should return success")
    public void api_success() {
        assertTrue(lastCode == 201 || lastCode == 200);
    }

    @When("the user submits the add-food form")
    public void submit_add_food_form() throws Exception {
        // build payload from lastFormData
        Food f = new Food();
        String name = lastFormData.getOrDefault("Name", "");
        String caloriesStr = lastFormData.getOrDefault("Calories", "0");
        String serving = lastFormData.getOrDefault("ServingSize", null);
        String qtyStr = lastFormData.getOrDefault("Quantity", "0");
        String unit = lastFormData.getOrDefault("Unit", null);
        f.setName(name);
        try { f.setCalories(Double.parseDouble(caloriesStr)); } catch (Exception e) { f.setCalories(0); }
        f.setServingSize(serving);
        try { f.setQuantity(Double.parseDouble(qtyStr)); } catch (Exception e) { f.setQuantity(0); }
        f.setUnit(unit);
        FoodService.ValidationResult vr = foodService.addFood(f);
        if (vr.success) { lastCode = 201; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    @When("the user submits the add-food form again with the same Name")
    public void submit_add_food_form_again() throws Exception {
        // reuse lastFormData (Name should be present)
        submit_add_food_form();
    }

    @When("the user updates the food named \"{string}\" with the following information:")
    public void user_updates_food_by_name(String existingName, DataTable table) throws Exception {
        // prepare update fields
        Map<String, String> update = new HashMap<>();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String field = row.get("Field");
            String value = row.get("Value");
            if (field != null) update.put(field.trim(), value == null ? "" : value);
        }

    // find existing food via service
    java.util.List<Food> list = foodService.getFoods();
        Food found = null;
    for (Food f : list) if (existingName.equals(f.getName())) { found = f; break; }
        if (found == null) throw new RuntimeException("Food not found: " + existingName);

        Food updated = new Food();
        if (update.containsKey("Name")) updated.setName(update.get("Name"));
        if (update.containsKey("Calories")) try { updated.setCalories(Double.parseDouble(update.get("Calories"))); } catch (Exception e) { updated.setCalories(0); }
        if (update.containsKey("ServingSize")) updated.setServingSize(update.get("ServingSize"));
        if (update.containsKey("Quantity")) try { updated.setQuantity(Double.parseDouble(update.get("Quantity"))); } catch (Exception e) { updated.setQuantity(0); }
        if (update.containsKey("Unit")) updated.setUnit(update.get("Unit"));

        FoodService.ValidationResult vr = foodService.updateFood(found.getId(), updated);
        if (vr.success) { lastCode = 200; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    @Then("the API should return an error containing \"{string}\"")
    public void api_error_contains(String fragment) throws Exception {
        // expect 4xx
        assertTrue(lastCode >= 400 && lastCode < 500, "Expected 4xx but was " + lastCode + " body:" + lastBody);
        if (lastBody != null) {
            assertTrue(lastBody.contains(fragment), "Expected response body to contain: " + fragment + " but was: " + lastBody);
        }
    }

    @When("I change the food named \"{string}\" to have name \"{string}\" and calories {int}")
    public void change_food_by_name(String currentName, String newName, int newCalories) throws Exception {
        java.util.List<Food> list = foodService.getFoods();
        Food found = null;
        for (Food f : list) { if (currentName.equals(f.getName())) { found = f; break; } }
        if (found == null) throw new RuntimeException("Food not found: " + currentName);

        Food updated = new Food();
        updated.setName(newName);
        updated.setCalories(newCalories);

        FoodService.ValidationResult vr = foodService.updateFood(found.getId(), updated);
        if (vr.success) { lastCode = 200; lastBody = vr.message; } else { lastCode = 400; lastBody = vr.message; }
    }

    // No HTTP body reading required when calling the service layer directly
}
