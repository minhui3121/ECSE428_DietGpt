package com.dietapp.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.dietapp.spring.repo.FoodRepository;
import com.dietapp.spring.repo.IngredientRepository;

public class MealPlanIntegrationTest {
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private IngredientRepository ingredientRepo;

    @Autowired
    private FoodRepository foodRepo;

    @AfterEach
    public void cleanup() {
        ingredientRepo.deleteAll();
        foodRepo.deleteAll();
    }

    @BeforeEach
    public void setup(){
        
    }

}
