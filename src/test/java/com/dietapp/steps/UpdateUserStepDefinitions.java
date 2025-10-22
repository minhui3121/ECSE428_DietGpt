package com.dietapp.steps;

import com.dietapp.model.User;
import com.dietapp.service.UserService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserStepDefinitions {

    private UserService userService = new UserService();
    private Map<String, User> testUsers = new HashMap<>();
    private String updateResultMessage;
    private User loggedInUser;

    @Given("user {string} with user id {string} is logged into the system")
    public void user_is_logged_into_the_system(String name, String userId) {
        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(name.toLowerCase() + "@email.com"); // Default email
        user.setAge(25); // Default age
        user.setWeight(70.0); // Default weight
        userService.createUser(user); // Create the user in the system
        loggedInUser = user;
    }

    @Given("the user has the following current profile:")
    public void the_user_has_the_following_current_profile(DataTable dataTable) {
        Map<String, String> profileData = dataTable.asMap(String.class, String.class);
        if (profileData.get("Name") != null) loggedInUser.setName(profileData.get("Name"));
        if (profileData.get("Email") != null) loggedInUser.setEmail(profileData.get("Email"));
        if (profileData.get("Age") != null) loggedInUser.setAge(Integer.parseInt(profileData.get("Age")));
        if (profileData.get("Weight") != null) loggedInUser.setWeight(Double.parseDouble(profileData.get("Weight")));
        if (profileData.get("Password") != null) loggedInUser.setPassword(profileData.get("Password"));
        userService.createUser(loggedInUser); // Update the user in the system
    }

    @Given("the user has the following current dietary preferences:")
    public void the_user_has_the_following_current_dietary_preferences(DataTable dataTable) {
        Map<String, String> preferences = dataTable.asMap(String.class, String.class);
        if (preferences.get("Vegetarian") != null) 
            loggedInUser.setVegetarian(Boolean.parseBoolean(preferences.get("Vegetarian")));
        if (preferences.get("Gluten-Free") != null) 
            loggedInUser.setGlutenFree(Boolean.parseBoolean(preferences.get("Gluten-Free")));
        userService.createUser(loggedInUser); // Update the user in the system
    }

    @When("the user updates the following profile information:")
    public void the_user_updates_the_following_profile_information(DataTable dataTable) {
        Map<String, String> updatedData = dataTable.asMap(String.class, String.class);
        User updatedUser = new User();
        updatedUser.setId(loggedInUser.getId());
        updatedUser.setName(updatedData.getOrDefault("Name", loggedInUser.getName()));
        updatedUser.setEmail(updatedData.getOrDefault("Email", loggedInUser.getEmail()));
        updatedUser.setAge(Integer.parseInt(updatedData.getOrDefault("Age", String.valueOf(loggedInUser.getAge()))));
        updatedUser.setWeight(Double.parseDouble(updatedData.getOrDefault("Weight", String.valueOf(loggedInUser.getWeight()))));
        updatedUser.setVegetarian(loggedInUser.isVegetarian());
        updatedUser.setGlutenFree(loggedInUser.isGlutenFree());
        
        updateResultMessage = userService.updateProfile(loggedInUser.getId(), updatedUser);
    }

    @When("the user updates their dietary preferences to:")
    public void the_user_updates_their_dietary_preferences_to(DataTable dataTable) {
        Map<String, String> updatedPreferences = dataTable.asMap(String.class, String.class);
        boolean vegetarian = Boolean.parseBoolean(updatedPreferences.get("Vegetarian"));
        boolean glutenFree = Boolean.parseBoolean(updatedPreferences.get("Gluten-Free"));
        
        updateResultMessage = userService.updateDietaryPreferences(loggedInUser.getId(), vegetarian, glutenFree);
    }

    @When("the user attempts to update their email to {string}")
    public void the_user_attempts_to_update_their_email_to(String email) {
        User updatedUser = new User();
        updatedUser.setId(loggedInUser.getId());
        updatedUser.setName(loggedInUser.getName());
        updatedUser.setEmail(email);
        updatedUser.setAge(loggedInUser.getAge());
        updatedUser.setWeight(loggedInUser.getWeight());
        updatedUser.setVegetarian(loggedInUser.isVegetarian());
        updatedUser.setGlutenFree(loggedInUser.isGlutenFree());
        
        updateResultMessage = userService.updateProfile(loggedInUser.getId(), updatedUser);
    }

    @Given("another user already has the email {string}")
    public void another_user_already_has_the_email(String email) {
        // Create another user with the existing email
        User existingUser = new User();
        existingUser.setId("999");
        existingUser.setName("Existing User");
        existingUser.setEmail(email);
        existingUser.setAge(30);
        existingUser.setWeight(70.0);
        userService.createUser(existingUser);
    }

    @When("user {string} attempts to update their email to {string}")
    public void user_attempts_to_update_their_email_to(String name, String email) {
        // Keep all existing user data, only update email
        User updatedUser = new User();
        updatedUser.setId(loggedInUser.getId());
        updatedUser.setName(loggedInUser.getName());
        updatedUser.setEmail(email);
        updatedUser.setAge(loggedInUser.getAge());
        updatedUser.setWeight(loggedInUser.getWeight());
        updatedUser.setVegetarian(loggedInUser.isVegetarian());
        updatedUser.setGlutenFree(loggedInUser.isGlutenFree());
        
        updateResultMessage = userService.updateProfile(loggedInUser.getId(), updatedUser);
    }

    @Then("the profile is successfully updated")
    public void the_profile_is_successfully_updated() {
        Assertions.assertEquals("Profile updated successfully", updateResultMessage);
    }

    @Then("the dietary preferences are successfully updated")
    public void the_dietary_preferences_are_successfully_updated() {
        Assertions.assertEquals("Dietary preferences updated successfully", updateResultMessage);
    }

    @Then("the system shows {string}")
    public void the_system_displays(String message) {
        Assertions.assertEquals(message, updateResultMessage);
    }

    @Then("the system should display an error message {string}")
    public void the_system_should_display_an_error_message(String errorMessage) {
        Assertions.assertEquals(errorMessage, updateResultMessage);
    }

    @Then("the profile should not be updated")
    public void the_profile_should_not_be_updated() {
        // Verify that the user's email hasn't changed
        User currentUser = userService.getUserByEmail(loggedInUser.getEmail());
        Assertions.assertNotNull(currentUser);
        Assertions.assertEquals(loggedInUser.getEmail(), currentUser.getEmail());
    }
}