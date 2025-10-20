Feature: Update User Profile

As a DietGPT User
I would like to update my user profile information
So that I can maintain accurate personal details and dietary preferences for better meal recommendations

Scenario: Successfully Update Basic Profile Information (Normal Flow)

Given user "Alice" with user id "101" is logged into the system
And the user has the following current profile:
      |Field        |Value           |
      |Name         |Alice Johnson   |
      |Password     |Hello111        |
      |Email        |alice@email.com |
      |Age          |28              |
      |Weight       |65              |
When the user updates the following profile information:
      |Field        |Value              |
      |Name         |Alice Smith        |
      |Email        |alice.smith@email.com |
      |Age          |29                 |
Then the profile is successfully updated
And the system displays "Profile updated successfully"


Scenario: Update Dietary Preferences (Alternate Flow)

Given user "Bob" with user id "102" is logged into the system
And the user has the following current dietary preferences:
      |Preference   |Status    |
      |Vegetarian   |false     |
      |Gluten-Free  |false     |
When the user updates their dietary preferences to:
      |Preference   |Status    |
      |Vegetarian   |true      |
      |Gluten-Free  |true      |
Then the dietary preferences are successfully updated
And the system displays "Dietary preferences updated successfully"


Scenario: Update Profile with Invalid Email Format (Error Flow)

Given user "Charlie" with user id "103" is logged into the system
When the user attempts to update their email to "invalid.email"
Then the system should display an error message "Invalid email format"
And the profile should not be updated


Scenario: Unauthorized User Attempts to Update Profile (Error Flow)

Given user "Unauthorized" is not logged into the system
When the user attempts to update their profile information
Then the system should display an error message "User authentication required"
And no profile changes should be made


Scenario: Update Profile with Duplicate Email (Error Flow)

Given user "Diana" with user id "104" is logged into the system
And another user already has the email "existing@email.com"
When user "Diana" attempts to update their email to "existing@email.com"
Then the system should display an error message "Email address already in use"
And the profile should not be updated
