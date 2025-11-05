Feature: ID010 Remove Meal History

As a DietGPT user
I would like to remove a meal record from my meal history
So that I can keep my nutrition log accurate and remove incorrect entries

Scenario: Successfully Remove a Meal Record (Normal Flow)

Given user "Alice" with user id "1" is logged into the system
And the user has the following meal history records:
    |Meal ID |Meal Type |Food Items      |Date |
    |101     |Breakfast |Oatmeal, Banana |2025-01-10 |
    |102     |Lunch     |Chicken Salad   |2025-01-10 |
When the user removes the meal record with Meal ID "102"
Then the system deletes the meal record successfully
And the system displays "Meal record removed successfully"
And the meal history should no longer contain Meal ID "102"


Scenario: Remove a Non-Existing Meal Record (Error Flow)

Given user "Bob" with user id "2" is logged into the system
And the user has the following meal history records:
    |Meal ID |Meal Type |Food Items      |Date |
    |201     | Dinner   |Pasta, Salad    |2025-01-08 |
When the user attempts to remove the meal record with Meal ID "9999"
Then the system displays an error message "Meal record not found"
And no meal record should be removed


Scenario: User Tries to Remove a Meal Record That Belongs to Another User (Error Flow)

Given user "Charlie" with user id "3" is logged into the system
And another user "David" owns a meal record with Meal ID "401"
When user "Charlie" attempts to remove the meal record with Meal ID "401"
Then the system displays an error message "Unauthorized action"
And the meal record should not be removed


Scenario: Remove Meal Record When History Is Empty (Boundary Flow)

Given user "Eve" with user id "5" is logged into the system
And the user has no meal history records
When the user attempts to remove a meal record
Then the system displays "No meal records available"
And nothing should be removed