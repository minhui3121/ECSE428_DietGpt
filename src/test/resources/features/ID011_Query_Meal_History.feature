Feature: ID011 Query Meal History

  As a DietGPT user
  I want to view meals from my meal history
  So that I can review, reuse, and analyze past meals

  Background:
    Given user "Alice" with user id "101" has the following meal history:
      | meal_id           | date       | meal_name            | ingredients                    | calories | tags               |
      | M-2025-11-01-001  | 2025-11-01 | Chicken Rice         | Chicken,Rice             | 620      | Non-Vegetarian     |
      | M-2025-11-02-001  | 2025-11-02 | Vegetable Stir-Fry   | Broccoli,Rice,Carrot     | 420      | Vegetarian,Gluten-Free |
      | M-2025-11-03-001  | 2025-11-03 | Oatmeal & Berries    | Oats,Milk,Berries        | 350      | Vegetarian         |
    And user "Bob" with user id "102" has the following meal history:
      | meal_id           | date       | meal_name         | ingredients                | calories | tags           |
      | M-2025-11-01-101  | 2025-11-01 | Pancakes          | Eggs,Milk,Flour      | 500      | Vegetarian     |
      | M-2025-11-02-101  | 2025-11-02 | Omelette          | Eggs,Cheese,Spinach  | 430      | Vegetarian     |

  # Normal flow
  Scenario: Retrieve meals within a date range (Normal Flow)
    When user "Alice" queries meal history from "2025-11-01" to "2025-11-02"
    Then the system returns the following meals for user "101":
      | date       | meal_name           |
      | 2025-11-01 | Chicken Rice        |
      | 2025-11-02 | Vegetable Stir-Fry  |
    And the result count is 2

  Scenario: Retrieve the most recent meal (Normal Flow)
    When user "Alice" requests the most recent meal
    Then the system returns a single meal for user "101" with:
      | field     | value               |
      | meal_name | Oatmeal & Berries   |
      | date      | 2025-11-03          |
      | calories  | 350                 |

  Scenario: Retrieve a meal by meal_id (Normal Flow)
    When the user "Alice" requests meal details for id "M-2025-11-02-001"
    Then the system returns meal details:
      | field     | value               |
      | meal_id   | M-2025-11-02-001    |
      | meal_name | Vegetable Stir-Fry  |
      | ingredients| Broccoli,Rice,Carrot|
      | calories  | 420                 |
      | tags      | Vegetarian,Gluten-Free |

  # Alternate flows
  Scenario: Filter meals by tag (dietary preference) (Alternate Flow)
    Given the user has the following dietary preference "Vegetarian"
    When user "Alice" queries meal history from "2025-11-01" to "2025-11-03" filtered by tag "Vegetarian"
    Then the system returns the following meals for user "101":
      | date       | meal_name           |
      | 2025-11-02 | Vegetable Stir-Fry  |
      | 2025-11-03 | Oatmeal & Berries   |
    And the result count is 2
    And the returned meals should not contain "Chicken Rice"

  # Error flows
  Scenario: No meals found in date range (Error Flow)
    When user "Alice" queries meal history from "2025-10-01" to "2025-10-07"
    Then the system should show a message "No meals found for the specified period"
    And the result count is 0

  Scenario: Meal not found by id (Error Flow)
    When the user "Alice" requests meal details for id "M-9999-01-01-XYZ"
    Then the system should show a message "Meal not found"
    And the profile should remain unchanged

  Scenario: Invalid date parameters (Error Flow)
    When user "Alice" queries meal history from "2025-11-10" to "2025-11-01"
    Then the system should show a message "Invalid date range"
    And the result count is 0
