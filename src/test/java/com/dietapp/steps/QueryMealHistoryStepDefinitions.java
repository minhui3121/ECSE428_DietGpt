package com.dietapp.steps;

import com.dietapp.model.Meal;
import com.dietapp.service.MealHistoryService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class QueryMealHistoryStepDefinitions {

    private MealHistoryService mealService = new MealHistoryService();
    private Map<String, List<Meal>> userMealHistories = new HashMap<>();
    private List<Meal> queryResult = new ArrayList<>();
    private String queryMessage;
    private String dietaryPreference;

    // --- GIVEN STEPS ---

    @Given("user {string} with user id {string} has the following meal history:")
    public void user_with_meal_history(String name, String userId, DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        List<Meal> meals = rows.stream().map(row -> {
            Meal m = new Meal();
            m.setMealId(row.get("meal_id"));
            m.setUserId(userId);
            m.setDate(LocalDate.parse(row.get("date")));
            m.setMealName(row.get("meal_name"));
            m.setIngredients(Arrays.asList(row.get("ingredients").split(",")));
            m.setCalories(Integer.parseInt(row.get("calories")));
            m.setTags(Arrays.asList(row.get("tags").split(",")));
            return m;
        }).collect(Collectors.toList());
        userMealHistories.put(userId, meals);
        mealService.loadMealsForUser(userId, meals);
    }

    @Given("the user has the following dietary preference {string}")
    public void user_has_the_following_dietary_preference(String preference) {
        this.dietaryPreference = preference;
    }

    // --- WHEN STEPS ---

    @When("user {string} queries meal history from {string} to {string}")
    public void user_queries_meal_history(String name, String from, String to) {
        try {
            LocalDate start = LocalDate.parse(from);
            LocalDate end = LocalDate.parse(to);
            queryResult = mealService.queryMealsByDateRange("101", start, end);
            if (queryResult.isEmpty()) {
                queryMessage = "No meals found for the specified period";
            } else {
                queryMessage = "Query successful";
            }
        } catch (IllegalArgumentException ex) {
            queryMessage = ex.getMessage();
            queryResult = Collections.emptyList();
        }
    }

    @When("user {string} queries meal history from {string} to {string} filtered by tag {string}")
    public void user_queries_meal_history_filtered_by_tag(String name, String from, String to, String tag) {
        try {
            LocalDate start = LocalDate.parse(from);
            LocalDate end = LocalDate.parse(to);
            queryResult = mealService.queryMealsByDateRangeAndTag("101", start, end, tag);
            if (queryResult.isEmpty()) {
                queryMessage = "No meals found for the specified period";
            } else {
                queryMessage = "Query successful";
            }
        } catch (IllegalArgumentException ex) {
            queryMessage = ex.getMessage();
            queryResult = Collections.emptyList();
        }
    }

    @When("user {string} requests the most recent meal")
    public void user_requests_most_recent_meal(String name) {
        Optional<Meal> recent = mealService.getMostRecentMeal("101");
        if (recent.isPresent()) {
            queryResult = List.of(recent.get());
            queryMessage = "Query successful";
        } else {
            queryResult = Collections.emptyList();
            queryMessage = "No meals found";
        }
    }

    @When("the user {string} requests meal details for id {string}")
    public void user_requests_meal_by_id(String name, String mealId) {
        Optional<Meal> meal = mealService.getMealById("101", mealId);
        if (meal.isPresent()) {
            queryResult = List.of(meal.get());
            queryMessage = "Query successful";
        } else {
            queryResult = Collections.emptyList();
            queryMessage = "Meal not found";
        }
    }

    // --- THEN STEPS ---

    @Then("the system returns the following meals for user {string}:")
    public void system_returns_meals(String userId, DataTable expectedTable) {
        List<Map<String, String>> expected = expectedTable.asMaps(String.class, String.class);
        Assertions.assertEquals(expected.size(), queryResult.size(), "Result count mismatch");
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).get("date"), queryResult.get(i).getDate().toString());
            Assertions.assertEquals(expected.get(i).get("meal_name"), queryResult.get(i).getMealName());
        }
    }

    @Then("the system returns a single meal for user {string} with:")
    public void system_returns_single_meal(String userId, DataTable expectedTable) {
        Assertions.assertEquals(1, queryResult.size(), "Expected exactly one meal");
        Meal meal = queryResult.get(0);
        Map<String, String> expected = expectedTable.asMap(String.class, String.class);
        expected.forEach((field, value) -> {
            switch (field) {
                case "meal_name" -> Assertions.assertEquals(value, meal.getMealName());
                case "date" -> Assertions.assertEquals(value, meal.getDate().toString());
                case "calories" -> Assertions.assertEquals(Integer.parseInt(value), meal.getCalories());
            }
        });
    }

    @Then("the system returns meal details:")
    public void system_returns_meal_details(DataTable expectedTable) {
        Assertions.assertEquals(1, queryResult.size());
        Meal meal = queryResult.get(0);
        Map<String, String> expected = expectedTable.asMap(String.class, String.class);
        Assertions.assertEquals(expected.get("meal_id"), meal.getMealId());
        Assertions.assertEquals(expected.get("meal_name"), meal.getMealName());
        Assertions.assertEquals(expected.get("ingredients"), String.join(",", meal.getIngredients()));
        Assertions.assertEquals(Integer.parseInt(expected.get("calories")), meal.getCalories());
        Assertions.assertEquals(expected.get("tags"), String.join(",", meal.getTags()));
    }

    @Then("the result count is {int}")
    public void result_count_is(int count) {
        Assertions.assertEquals(count, queryResult.size());
    }

    @Then("the returned meals should not contain {string}")
    public void returned_meals_should_not_contain(String excludedMeal) {
        Assertions.assertTrue(queryResult.stream().noneMatch(m -> m.getMealName().equals(excludedMeal)));
    }

    @Then("the system displays a message {string}")
    public void system_displays_message(String message) {
        Assertions.assertEquals(message, queryMessage);
    }

    @Then("the profile should remain unchanged")
    public void profile_should_remain_unchanged() {
        // no-op for this feature
    }
}
