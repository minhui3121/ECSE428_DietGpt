Feature: ID008 Remove Ingredient

  As a DietGPT user
  I want to remove an existing ingredient
  So that I can manage my recipes and meal plans more effectively

  Background:
    Given the ingredient store is not empty

  # Normal flow
  Scenario: Remove an existing ingredient successfully
    Given an ingredient named "Rice" exists in the system
    When I remove ingredient "Rice"
    Then the system should not contain an ingredient named "Rice"

  # Error flows and validations
  Scenario: Remove non-existing ingredient
    When I remove ingredient "DoesNotExist"
    Then I should see an error "Ingredient does not exist"

  Scenario: Reject removal with empty ingredient name (validation)
    When I remove ingredient ""
    Then I should see an error "Invalid ingredient name"

  Scenario: Prevent removal when ingredient is in use by a food (dependency)
    Given a food "Fried Rice" exists that uses ingredient "Rice"
    And an ingredient named "Rice" exists in the system
    When I remove ingredient "Rice"
    Then I should see an error "Ingredient is in use"
    And the system should still contain an ingredient named "Rice"

  Scenario: Removal trims whitespace from name
    Given an ingredient named "Salt" exists in the system
    When I remove ingredient "  Salt  "
    Then the system should not contain an ingredient named "Salt"

  Scenario: Case-insensitive removal (name matching)
    Given an ingredient named "Sugar" exists in the system
    When I remove ingredient "sUgAr"
    Then the system should not contain an ingredient named "Sugar"

  # Authorization / permission (optional flow to drive tests)
  Scenario: Unauthorized user cannot remove ingredient
    Given an ingredient named "Pepper" exists in the system
    And I am an unauthenticated user
    When I remove ingredient "Pepper"
    Then I should see an error "Unauthorized"
    And the system should still contain an ingredient named "Pepper"

  # Edge case: concurrent dependency added after check (integration test candidate)
  Scenario: Fail removal when dependency is added concurrently
    Given an ingredient named "Tomato" exists in the system
    And another process adds a food "Tomato Soup" that uses "Tomato" between check and delete
    When I remove ingredient "Tomato"
    Then I should see an error "Ingredient is in use"
    And the system should still contain an ingredient named "Tomato"
  