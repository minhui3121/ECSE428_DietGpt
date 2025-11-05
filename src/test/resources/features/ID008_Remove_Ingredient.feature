Feature: ID008 Remove Ingredient

  As a DietGPT user
  I want to remove an existing ingredient
  So that I can manage my recipes and meal plans more effectively

  Background:
    Given the ingredient store is not empty

  Scenario: Remove an existing ingredient successfully (Normal Flow)
    Given an ingredient named "Rice" exists in the system
    When I remove ingredient "Rice"
    Then the system should not contain an ingredient named "Rice"

  Scenario: Remove non-existing ingredient (Error Flow)
    When I remove ingredient "DoesNotExist"
    Then I should see an error "Ingredient does not exist"
