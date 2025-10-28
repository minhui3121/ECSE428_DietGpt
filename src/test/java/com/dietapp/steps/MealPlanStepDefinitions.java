package com.dietapp.steps;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import com.dietapp.model.Food;
import com.dietapp.model.Ingredient;
import com.dietapp.model.User;
import com.dietapp.service.FoodService;
import com.dietapp.service.IngredientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MealPlanStepDefinitions {

    private final FoodService foodService = new FoodService();
    private final IngredientService ingredientService = new IngredientService();

    private User currentUser;
    private List<String> generatedMealPlan;
    private String systemMessage;

    @Given("user {string} with user id {string} has the following ingredients:")
    public void user_has_ingredients(String userName, String userId, io.cucumber.datatable.DataTable table) {
        currentUser = new User();
        currentUser.setId(userId);
        currentUser.setName(userName);
        List<Ingredient> userIngredients = new ArrayList<>();
        for (String name : table.asList()) {
            Ingredient ing = new Ingredient();
            ing.setName(name);
            userIngredients.add(ing);
        }
        userIngredients.forEach(ingredientService::addIngredient);
    }

    @And("the user has a dietary preference {string}")
    public void set_dietary_preference(String dietType) {
        if (dietType.equalsIgnoreCase("Vegetarian")) {
            currentUser.setVegetarian(true);
        }
        if (dietType.equalsIgnoreCase("Gluten-Free")) {
            currentUser.setGlutenFree(true);
        }
    }

    @And("the system has the following recipes:")
    public void system_has_recipes(io.cucumber.datatable.DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            Food food = new Food();
            food.setName(row.getOrDefault("food_name", ""));
            
            String ingredientsCell = row.getOrDefault("required_ingredients", "");
            food.setIngredients(ingredientsCell.isEmpty() ? List.of() : List.of(ingredientsCell.split(",")));

            String dietType = row.get("diet_type");
            if (dietType != null && !dietType.isEmpty()) {
                food.setDietaryTags(List.of(dietType));
            }

            foodService.addFood(food);
        }
    }


    @When("user {string} generates a daily meal plan")
    public void generate_meal_plan(String userName) {
        generatedMealPlan = new ArrayList<>();
        List<Food> foods = foodService.getFoods();

        for (Food f : foods) {
            // check if user's ingredients cover the recipe
            boolean hasAllIngredients = ingredientService.getIngredients().stream()
                    .map(Ingredient::getName)
                    .toList()
                    .containsAll(f.getIngredients());

            // check dietary preference
            boolean matchesDiet = true;
            if (currentUser.isVegetarian()) {
                matchesDiet = f.getDietaryTags().contains("Vegetarian");
            }

            if (hasAllIngredients && matchesDiet) {
                generatedMealPlan.add(f.getName());
            }
        }

        if (generatedMealPlan.isEmpty()) {
            systemMessage = "No available recipes match the current ingredients";
        } else {
            systemMessage = "Meal plan generated successfully";
        }
    }

    @Then("a meal plan containing {string} is generated")
    public void meal_plan_contains(String foodName) {
        assertTrue(generatedMealPlan.contains(foodName));
    }

    @Then("the system should display a message {string}")
    public void check_system_message(String message) {
        assertEquals(message, systemMessage);
    }

    @Then("the generated meal plan should not contain {string}")
    public void meal_plan_not_contains(String foodName) {
        assertFalse(generatedMealPlan.contains(foodName));
    }
}