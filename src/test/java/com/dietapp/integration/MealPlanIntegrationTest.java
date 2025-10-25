package com.dietapp.integration;

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

    public void cleanup() {
        ingredientRepo.deleteAll();
        foodRepo.deleteAll();
    }

}
