Feature: ID007 Add New Ingredient

  As a DietGPT user
  I want to add a new ingredient with a unit
  So that I can use it in my recipes and meal plans

  Background:
    Given the ingredient store is empty

  Scenario: Add a new ingredient successfully
    When I add ingredient "Rice" with unit "GRAM"
    Then the system should contain an ingredient named "Rice" with unit "GRAM"

  Scenario: Prevent duplicate ingredient (case-insensitive)
    Given I have added ingredient "Milk" with unit "LITER"
    When I add ingredient "milk" with unit "LITER"
    Then I should see an error "Ingredient already exists"

  Scenario: Reject invalid unit
    When I add ingredient "Bottle" with unit "BOTTLE"
    Then I should see an error "Invalid unit"
