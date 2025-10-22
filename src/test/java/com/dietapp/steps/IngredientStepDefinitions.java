package com.dietapp.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientStepDefinitions {

    @Autowired
    private TestRestTemplate rest; // should be injected when context boots

    private ResponseEntity<Map> lastResponse;

    @Given("the ingredient store is empty")
    public void clearStore() {
        assertNotNull(rest, "TestRestTemplate not injected; check Cucumber-Spring setup/glue.");
        ResponseEntity<Map[]> res = rest.getForEntity("/api/ingredients", Map[].class);
        if (res.getBody() != null) {
            for (Map m : res.getBody()) {
                rest.delete("/api/ingredients/" + m.get("id"));
            }
        }
    }

    @When("I add ingredient {string} with unit {string}")
    public void addIngredient(String name, String unit) {
        Map<String, String> payload = Map.of("name", name, "unit", unit);
        lastResponse = rest.postForEntity("/api/ingredients", payload, Map.class);
    }

    @Given("I have added ingredient {string} with unit {string}")
    public void haveAdded(String name, String unit) {
        addIngredient(name, unit);
        assertTrue(lastResponse.getStatusCode().is2xxSuccessful());
    }

    @Then("the system should contain an ingredient named {string} with unit {string}")
    public void shouldContain(String name, String unit) {
        ResponseEntity<Map[]> res = rest.getForEntity("/api/ingredients", Map[].class);
        boolean found = false;
        if (res.getBody() != null) {
            for (Map m : res.getBody()) {
                if (name.equals(m.get("name")) && unit.equals(m.get("unit"))) {
                    found = true; break;
                }
            }
        }
        assertTrue(found, "Ingredient not found in list");
    }

    @Then("I should see an error {string}")
    public void shouldSeeError(String err) {
        assertEquals(HttpStatus.BAD_REQUEST, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        String msg = String.valueOf(lastResponse.getBody().get("error"));
        assertTrue(msg.toLowerCase().contains(err.toLowerCase()));
    }
}
