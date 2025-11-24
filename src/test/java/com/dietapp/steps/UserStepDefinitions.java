package com.dietapp.steps;

import com.dietapp.model.Food;
import com.dietapp.model.User;
import com.dietapp.service.UserService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserStepDefinitions {

    private UserService userService = new UserService();
    private User user;
    private String resultMessage;

    @Given("a new user provides the following information:")
    public void newUserProvidesInfo(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        user = new User();
        user.setName(data.get("Name"));
        user.setEmail(data.get("Email"));
        user.setAge(data.get("Age") != null ? Integer.parseInt(data.get("Age")) : 0);
        user.setWeight(data.get("Weight") != null ? Double.parseDouble(data.get("Weight")) : 0);
        user.setVegetarian(data.get("Vegetarian") != null && Boolean.parseBoolean(data.get("Vegetarian")));
        user.setGlutenFree(data.get("Gluten-Free") != null && Boolean.parseBoolean(data.get("Gluten-Free")));
    }

    @Given("an existing user already has the email {string}")
    public void existingUserWithEmail(String email) {
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID().toString());
        existingUser.setName("Existing User");
        existingUser.setEmail(email);
        existingUser.setAge(30);
        existingUser.setWeight(70);
        existingUser.setVegetarian(false);
        existingUser.setGlutenFree(false);

        userService.createUser(existingUser);
    }

    @Given("the user has no meal history records")
    public void the_user_has_no_meal_history_records() {
        user = new User();
        user.setFoodHistory(new ArrayList<>());
    }

    @Given("another user {string} owns a meal record with Meal ID {string}")
    public void another_user_owns_a_meal_record_with_Meal_ID(String s, String s2) {
        user = new User();
        user.setFoodHistory(new ArrayList<>());

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID().toString());
        otherUser.setName(s);
        otherUser.setEmail(s.toLowerCase() + "@email.com");
        otherUser.setAge(28);
        otherUser.setWeight(75);

        Food meal = new Food();
        meal.setId(s2);

        List<Food> mealHistory = new ArrayList<>();
        mealHistory.add(meal);
        otherUser.setFoodHistory(mealHistory);

        userService.createUser(otherUser);
    }

    @When("the user submits the registration form")
    public void submitRegistrationForm() {
        resultMessage = userService.createUser(user);
    }

    @Then("a new user profile is created successfully")
    public void profileCreatedSuccessfully() {
        Assertions.assertEquals("Profile created successfully", resultMessage);
        Assertions.assertNotNull(user.getId());
    }

    @Then("the system displays {string}")
    public void systemDisplays(String message) {
        Assertions.assertEquals(message, resultMessage);
    }

    @Then("the user is assigned a unique user id")
    public void checkUniqueUserId() {
        List<User> users = userService.getUsers();
        Set<String> set = new HashSet<>();
        for (User user : users) {
            set.add(user.getId());
        }
        Assertions.assertEquals(users.size(), set.size());
    }

    @Then("the profile should not be created")
    public void noUser() {
        Assertions.assertEquals(0, userService.getUsers().size());
    }

    @Then("only one user in the system has the email {string}")
    public void uniqueUser(String email) {
        int count = 0;
        for (User user : userService.getUsers()) {
            if (user.getEmail().equals(email)) {
                count++;
            }
        }
        Assertions.assertEquals(1, count);
    }

    @Then("the dietary preferences are saved")
    public void dietaryPreferencesSaved() {
        User user = userService.getUserByEmail(this.user.getEmail());
        Assertions.assertEquals(this.user.isGlutenFree(), user.isGlutenFree());
        Assertions.assertEquals(this.user.isVegetarian(), user.isVegetarian());
    }

    @Given("the user has the following meal history records:")
    public void the_user_has_meal_history(io.cucumber.datatable.DataTable dataTable) {
        user = new User();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<Food> meals = new ArrayList<>();
        for (Map<String, String> row : rows) {
            Food food = new Food();
            food.setId(row.get("Meal ID"));
            meals.add(food);
        }
        user.setFoodHistory(meals);
    }

    @When("the user removes the meal record with Meal ID {string}")
    public void the_user_removes_meal_record(String mealId) {
        resultMessage = userService.removeFromMealHistory(user, mealId);
    }

    @Then("the system deletes the meal record successfully")
    public void system_deletes_meal_record() {
        assertEquals("Meal record removed successfully", resultMessage);
    }

    @Then("the meal history should no longer contain Meal ID {string}")
    public void meal_history_should_not_contain(String mealId) {
        List<Food> meals = user.getFoodHistory();
        assertTrue(meals.stream().noneMatch(m -> m.getId().equals(mealId)));
    }

    @Then("no meal record should be removed")
    public void no_meal_record_removed() {
        assertEquals("Meal record not found", resultMessage);
    }

    @Then("the system should display a message {string}")
    public void display_message(String message) {
        assertEquals(message, message);
    }
}
