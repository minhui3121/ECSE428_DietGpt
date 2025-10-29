Feature: Generate Daily Meal Plan

  As a DietGpt User
  I would like to generate a daily meal plan
  So that I can know what to eat while making efficient use of my ingredients

  #normal flow
  Scenario Outline: Successful Meal Plan Generation
    Given user "<user_name>" with user id "<user_id>" has the following ingredients: "<ingredient_list>"
    And the system has a recipe "<food_name>" that requires: "<required_ingredients>"
    When user "<user_name>" generates a daily meal plan
    Then a meal plan containing "<food_suggestion>" is generated

    Examples:
      | user_name | user_id | ingredient_list           | food_name    | required_ingredients | food_suggestion |
      | Alice     | 101     | Chicken,Rice,Broccoli     | Chicken Rice | Chicken,Rice         | Chicken Rice    |
      | Bob       | 102     | Eggs,Milk,Flour           | Pancakes     | Eggs,Milk,Flour      | Pancakes        |

  #error flow
  Scenario: Generate Meal Plan Fails Due to Insufficient Ingredients
    Given user "Alice" with user id "101" has the following ingredients:
      | ingredient |
      | Lettuce    |
      | Tomato     |
    And the system has the following recipes:
      | food_name      | required_ingredients |
      | Chicken Rice   | Chicken,Rice         |
      | Pancakes       | Eggs,Milk,Flour      |
    When user "Alice" generates a daily meal plan
    Then the system should display a message "No available recipes match the current ingredients"

  #alternate flow
  Scenario: Meal Plan Generated According to Dietary Restriction
    Given user "Bob" with user id "102" has the following ingredients:
      | Chicken    |
      | Rice       |
      | Broccoli   |
    And the user has a dietary preference "Vegetarian"
    And the system has the following recipes:
      | food_name          | required_ingredients | diet_type      |
      | Chicken Rice       | Chicken,Rice         | Non-Vegetarian |
      | Vegetable Stir-Fry | Broccoli,Rice        | Vegetarian     |
    When user "Bob" generates a daily meal plan
    Then a meal plan containing "Vegetable Stir-Fry" is generated
    And the generated meal plan should not contain "Chicken Rice"
