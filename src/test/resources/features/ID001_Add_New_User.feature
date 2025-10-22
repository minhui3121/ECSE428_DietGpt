Feature: Add New User

As a new DietGPT User
I would like to create a user profile information
So that I can maintain accurate personal details and dietary preferences for better meal recommendations

Scenario: Successfully Create Basic Profile Information (Normal Flow)

Given a new user provides the following information:
    |Field        |Value           |
    |Name         |Alice Johnson   |
    |Email        |alice@email.com |
    |Age          |28              |
    |Weight       |65              |
When the user submits the registration form
Then a new user profile is created successfully
And the system displays "Profile created successfully"
And the user is assigned a unique user id


Scenario: Create Profile with Missing Fields (Error flow)

Given a new user provides the following information:
    |Field        |Value           |
    |Name         |                |
    |Email        |alice@email.com |
    |Age          |28              |
    |Weight       |65              |
    |Vegetarian   |false           |
    |Gluten-Free  |false           |
When the user submits the registration form
Then the system displays "Please fill in all required fields"
And the profile should not be created


Scenario: Create Profile with Invalid Email Format (Error flow)

Given a new user provides the following information:
    |Field        |Value           |
    |Name         |Alice           |
    |Email        |alice.com       |
    |Age          |28              |
    |Weight       |65              |
    |Vegetarian   |false           |
    |Gluten-Free  |false           |
When the user submits the registration form
Then the system displays "Invalid email format"
And the profile should not be created


Scenario: Create Profile with Duplicate Email (Error flow)

Given an existing user already has the email "alex@email.com"
And a new user provides the following information:
    |Field        |Value           |
    |Name         |Alice           |
    |Password     |Hello111        |
    |Email        |alex@email.com  |
    |Age          |30              |
    |Weight       |75              |
    |Vegetarian   |false           |
    |Gluten-Free  |false           |
When the user submits the registration form
Then the system displays "Email address already in use"
And only one user in the system has the email "alex@email.com"


Scenario: Create Profile with Dietary Preferences (Alternate flow)

Given a new user provides the following information:
    |Field        |Value           |
    |Name         |Alice Johnson   |
    |Password     |Hello111        |
    |Email        |alice@email.com |
    |Age          |28              |
    |Weight       |65              |
    |Vegetarian   |false           |
    |Gluten-Free  |false           |
When the user submits the registration form
Then a new user profile is created successfully
And the system displays "Profile created successfully"
And the dietary preferences are saved
