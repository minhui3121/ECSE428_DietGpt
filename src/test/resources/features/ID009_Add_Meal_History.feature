Feature: ID009 Add Meal History

As a DietGPT user
I would like to add a meal record to my meal history
So that I can track what I eat on a daily basis

Scenario: Successfully Add a Meal Record (Normal Flow)
Given user "Alice" with user id "1" is logged into the system
And the user currently has the following meal history records:
    | Meal ID | Meal Type | Food Items      | Date       |
    | 101     | Breakfast | Oatmeal, Banana | 2025-01-10 |
When the user adds a new meal history record:
    | Meal ID | Meal Type | Food Items      | Date       |
    | 102     | Lunch     | Chicken Salad   | 2025-01-10 |
Then the meal record is added successfully
And the system displays "Meal record added successfully"
And the meal history should contain Meal ID "102"

Scenario: Add a Meal Record With Duplicate Meal ID (Error Flow)
Given user "Bob" with user id "2" is logged into the system
And the user already has the following meal history records:
    | Meal ID | Meal Type | Food Items     | Date       |
    | 201     | Dinner    | Pasta, Salad   | 2025-01-08 |
When the user attempts to add a new meal history record:
    | Meal ID | Meal Type | Food Items     | Date       |
    | 201     | Lunch     | Sushi, Tea     | 2025-01-09 |
Then the system displays an error message "Meal ID already exists"
And no new meal record should be added

Scenario: Add Meal Record When User Has No History (Boundary Flow)
Given user "Charlie" with user id "3" is logged into the system
And the user has no meal history records
When the user adds a new meal history record:
    | Meal ID | Meal Type | Food Items         | Date       |
    | 301     | Breakfast | Toast, Orange Juice| 2025-01-11 |
Then the meal record is added successfully
And the system displays "Meal record added successfully"
And the meal history should contain Meal ID "301"
