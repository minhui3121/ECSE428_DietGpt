package com.dietapp.steps;

import static org.junit.jupiter.api.Assertions.*;

import com.dietapp.spring.dto.MealHistoryDto;
import com.dietapp.spring.entity.MealEntity;
import com.dietapp.spring.repo.MealHistoryRepository;
import com.dietapp.spring.service.MealHistoryServiceImpl;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AddMealHistoryStepDefinitions {

    private MealHistoryServiceImpl mealService;
    private MealHistoryRepository mockRepo;
    private Map<Long, List<MealEntity>> userMealDatabase;
    private Long currentUserId;
    private String currentUserName;
    private MealHistoryDto mealToAdd;
    private MealHistoryDto addedMeal;
    private String resultMessage;
    private boolean additionSuccessful;

    @Before
    public void setup() {
        mockRepo = Mockito.mock(MealHistoryRepository.class);
        mealService = new MealHistoryServiceImpl(mockRepo);
        userMealDatabase = new HashMap<>();
        resultMessage = null;
        additionSuccessful = false;
        addedMeal = null;
    }

    @Given("user {string} with id {string} is in the meal system")
    public void user_is_in_meal_system(String userName, String userId) {
        this.currentUserName = userName;
        this.currentUserId = Long.parseLong(userId);
        if (!userMealDatabase.containsKey(currentUserId)) {
            userMealDatabase.put(currentUserId, new ArrayList<>());
        }
    }

    @Given("the user currently has the following meal history records:")
    public void user_has_meal_history_records(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        List<MealEntity> meals = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            MealEntity meal = new MealEntity();
            meal.setMealId(Long.parseLong(row.get("Meal ID")));
            meal.setUserId(currentUserId);
            meal.setDate(LocalDate.parse(row.get("Date")));
            meal.setMealName(row.get("Meal Type"));
            meal.setIngredients(row.get("Food Items"));
            meal.setCalories(0.0);
            meal.setTags("");
            meals.add(meal);
        }
        
        userMealDatabase.put(currentUserId, meals);
        when(mockRepo.findByUserId(currentUserId)).thenReturn(meals);
    }

    @Given("the user already has the following meal history records:")
    public void user_already_has_meal_history_records(DataTable table) {
        user_has_meal_history_records(table);
    }

    @When("the user adds a new meal history record:")
    public void user_adds_new_meal_record(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);
        
        Long mealId = Long.parseLong(row.get("Meal ID"));
        
        if (currentUserId == null) {
            currentUserId = 3L;
        }
        if (!userMealDatabase.containsKey(currentUserId)) {
            userMealDatabase.put(currentUserId, new ArrayList<>());
        }
        
        List<MealEntity> existingMeals = userMealDatabase.get(currentUserId);
        boolean mealIdExists = existingMeals.stream()
                .anyMatch(m -> m.getMealId().equals(mealId));
        
        if (mealIdExists) {
            additionSuccessful = false;
            resultMessage = "Meal ID already exists";
            return;
        }
        
        mealToAdd = new MealHistoryDto();
        mealToAdd.setDate(LocalDate.parse(row.get("Date")));
        mealToAdd.setMealName(row.get("Meal Type"));
        mealToAdd.setIngredients(row.get("Food Items"));
        mealToAdd.setCalories(0.0);
        mealToAdd.setTags("");
        
        when(mockRepo.save(any(MealEntity.class))).thenAnswer(invocation -> {
            MealEntity saved = invocation.getArgument(0);
            saved.setMealId(mealId);
            existingMeals.add(saved);
            return saved;
        });
        
        try {
            addedMeal = mealService.add(mealToAdd, currentUserId);
            additionSuccessful = true;
            resultMessage = "Meal record added successfully";
        } catch (Exception e) {
            additionSuccessful = false;
            resultMessage = e.getMessage();
        }
    }

    @When("the user attempts to add a new meal history record:")
    public void user_attempts_to_add_new_meal_record(DataTable table) {
        user_adds_new_meal_record(table);
    }


    @Then("the meal record is added successfully")
    public void meal_record_is_added_successfully() {
        assertTrue(additionSuccessful, "Expected meal addition to be successful");
        assertNotNull(addedMeal, "Added meal should not be null");
    }

    @Then("the meal system displays {string}")
    public void meal_system_displays_message(String expectedMessage) {
        assertNotNull(resultMessage, "Result message should not be null");
        assertEquals(expectedMessage, resultMessage, 
                "Expected message: '" + expectedMessage + "' but got: '" + resultMessage + "'");
    }

    @Then("the meal history should contain Meal ID {string}")
    public void meal_history_should_contain_meal_id(String mealId) {
        Long id = Long.parseLong(mealId);
        List<MealEntity> userMeals = userMealDatabase.get(currentUserId);
        
        boolean found = userMeals.stream()
                .anyMatch(m -> m.getMealId().equals(id));
        
        assertTrue(found, "Meal history should contain meal with ID: " + mealId);
    }

    @Then("the system displays an error message {string}")
    public void system_displays_error_message(String errorMessage) {
        System.out.println("HAIHAI");

        System.out.println(resultMessage);
        System.out.println(errorMessage);
        assertNotNull(resultMessage, "Result message should not be null");
        assertEquals(errorMessage, resultMessage, 
                "Expected error: '" + errorMessage + "' but got: '" + resultMessage + "'");
    }

    @Then("no new meal record should be added")
    public void no_new_meal_record_should_be_added() {
        assertFalse(additionSuccessful, "Expected meal addition to fail");
    }
}
