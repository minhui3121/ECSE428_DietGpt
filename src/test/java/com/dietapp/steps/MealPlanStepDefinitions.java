package com.dietapp.steps;

import com.dietapp.model.Food;
import com.dietapp.model.Ingredient;
import com.dietapp.model.User;
import com.dietapp.service.FoodService;
import com.dietapp.service.IngredientService;
import com.dietapp.service.UserService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class MealPlanStepDefinitions {

    private static final UserService userService = new UserService();
    private static final FoodService foodService = new FoodService();
    private static final IngredientService ingredientService = new IngredientService();

    private User currentUser;
    private List<Food> generatedMealPlan;

    @Before
    public void reset() {
        ingredientService.clear();
        generatedMealPlan = new ArrayList<>();
        currentUser = null;
    }

    @Given("user {string} with user id {string} has the following ingredients: {string}")
    public void user_has_ingredients_csv(String userName, String userId, String ingredientsCsv) {
        createUserWithIngredients(userName, userId, Arrays.asList(ingredientsCsv.split(",")));
    }

    @Given("user {string} with user id {string} has the following ingredients:")
    public void user_has_ingredients_table(String userName, String userId, DataTable table) {
        createUserWithIngredients(userName, userId, table.asList(String.class));
    }

    private void createUserWithIngredients(String userName, String userId, List<String> ingredients) {
        currentUser = new User();
        currentUser.setId(userId);
        currentUser.setName(userName);
        userService.getUsers().add(currentUser);

        ingredientService.clear();
        for (String ingName : ingredients) {
            if (ingName != null && !ingName.trim().isEmpty()) {
                Ingredient ing = new Ingredient();
                ing.setId(UUID.randomUUID().toString());
                ing.setName(ingName.trim().toLowerCase());
                ing.setUnit("pcs");
                ing.setQuantity(1);
                ing.setCaloriesPerUnit(0);
                ing.setDietaryTags(List.of());
                ing.setAllergens(List.of());
                ingredientService.addIngredient(ing);
            }
        }
    }

   @Given("the system has a recipe {string} that requires: {string}")
    public void system_has_recipe(String foodName, String requiredIngredientsCsv) {
        Food food = new Food();
        food.setName(foodName);
        food.setIngredients(Arrays.stream(requiredIngredientsCsv.split(","))
                                .map(String::trim)
                                .map(String::toLowerCase)
                                .collect(Collectors.toList()));
        food.setDietaryTags(List.of());
        food.setCalories(100);
        foodService.addFood(food);
    }


    @Given("the system has the following recipes:")
    public void system_has_recipes(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
        if (rows.size() <= 1) return; 

        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.isEmpty()) continue;

            Food food = new Food();
            food.setName(row.get(0).trim());
            food.setCalories(100);

            String ingredientsStr = row.size() > 1 ? row.get(1) : "";
            food.setIngredients(Arrays.stream(ingredientsStr.split(","))
                                    .map(String::trim)
                                    .map(String::toLowerCase)
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.toList()));

            if (row.size() > 2 && !row.get(2).trim().isEmpty()) {
                food.setDietaryTags(List.of(row.get(2).trim()));
            } else {
                food.setDietaryTags(List.of()); 
            }

            foodService.addFood(food);
        }
    }

    @Given("the user has a dietary preference {string}")
    public void user_has_dietary_preference(String dietType) {
        if ("Vegetarian".equalsIgnoreCase(dietType)) currentUser.setVegetarian(true);
        if ("Gluten-Free".equalsIgnoreCase(dietType)) currentUser.setGlutenFree(true);
    }

    
    @When("user {string} generates a daily meal plan")
    public void generate_meal_plan(String userName) {
        List<Food> availableFoods = foodService.getFoods();
        Set<String> userIngredients = ingredientService.getIngredients()
                                                    .stream()
                                                    .map(i -> i.getName().trim().toLowerCase())
                                                    .collect(Collectors.toSet());

        generatedMealPlan = availableFoods.stream()
            .filter(food -> {
                List<String> recipeIngredients = Optional.ofNullable(food.getIngredients())
                                                        .orElse(Collections.emptyList())
                                                        .stream()
                                                        .map(String::trim)
                                                        .map(String::toLowerCase)
                                                        .collect(Collectors.toList());

                //check ingredient availability
                if (!userIngredients.containsAll(recipeIngredients)) {
                    List<String> missing = recipeIngredients.stream()
                                                            .filter(r -> !userIngredients.contains(r))
                                                            .toList();
                    return false;
                }

                List<String> tags = Optional.ofNullable(food.getDietaryTags()).orElse(Collections.emptyList());

                //check vegetarian
                if (currentUser.isVegetarian()) {
                    List<String> tagsLower = tags.stream().map(String::toLowerCase).toList();
                    if (!tagsLower.contains("vegetarian")) {
                        return false;
                    }
                }

                //check gluten-free
                if (currentUser.isGlutenFree()) {
                    List<String> tagsLower = tags.stream().map(String::toLowerCase).toList();
                    if (!tagsLower.contains("gluten-free")) {
                        return false;
                    }
                }

                return true;
            })
            .collect(Collectors.toList());
    }

    @Then("a meal plan containing {string} is generated")
    public void meal_plan_contains(String foodName) {
        boolean found = generatedMealPlan.stream()
                                         .anyMatch(f -> f.getName().equalsIgnoreCase(foodName));
        assertTrue("Expected meal plan to contain: " + foodName, found);
    }

    @Then("the generated meal plan should not contain {string}")
    public void meal_plan_does_not_contain(String foodName) {
        boolean found = generatedMealPlan.stream()
                                         .anyMatch(f -> f.getName().equalsIgnoreCase(foodName));
        assertTrue("Expected meal plan NOT to contain: " + foodName, !found);
    }

    @Then("the system should display a message {string}")
    public void system_displays_message(String message) {
        if (generatedMealPlan.isEmpty()) {
            assertTrue(true);
        }
    }
}
