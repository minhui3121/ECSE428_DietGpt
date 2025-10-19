Feature: Add New Food

As a user of DietApp
I want to add foods to my personal food database
So that I can use them when generating meal plans and tracking ingredients

Scenario: Successfully add a basic food (Normal Flow)

  Given a user provides the following information:
    | Field       | Value   |
    | Name        | Orange  |
    | Calories    | 47      |
    | ServingSize | 100g    |
    | Quantity    | 5       |
    | Unit        | pcs     |
  When the user submits the add-food form
  Then the API should return success

Scenario: Add food with missing required field (Error flow)

  Given a user provides the following information:
    | Field       | Value |
    | Name        |       |
    | Calories    | 10    |
  When the user submits the add-food form
  Then the API should return an error containing "Name is required"

Scenario: Add food with invalid calories (Error flow)

  Given a user provides the following information:
    | Field       | Value |
    | Name        | BadCal|
    | Calories    | 0     |
  When the user submits the add-food form
  Then the API should return an error containing "Calories must be positive"

Scenario: Add duplicate food name (Error flow)

  Given a user provides the following information:
    | Field    | Value  |
    | Name     | Banana |
    | Calories | 89     |
  When the user submits the add-food form
  And the user submits the add-food form again with the same Name
  Then the API should return an error containing "already exists"

Scenario: Edit (alter) existing food (Alternate flow)

  Given a user provides the following information:
    | Field    | Value |
    | Name     | Pear  |
    | Calories | 57    |
  When the user submits the add-food form
  And the user updates the food named "Pear" with the following information:
    | Field    | Value        |
    | Name     | Pear (ripe)  |
    | Calories | 60           |
  Then the API should return success

