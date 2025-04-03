Feature: User Authentication
  As a user of the application
  I want to be able to login and logout
  So that I can access my account securely

  Background:
    Given I am on the login page

  @smoke @regression
  Scenario: Successful login with valid credentials
    When I enter username "validUser"
    And I enter password "validPass123"
    And I click the login button
    Then I should be redirected to the dashboard
    And I should see a welcome message "Welcome, Valid User"

  @regression
  Scenario Outline: Login with invalid credentials
    When I enter username "<username>"
    And I enter password "<password>"
    And I click the login button
    Then I should see error message "<error_message>"

    Examples:
      | username  | password    | error_message                    |
      | invalid   | wrong       | Invalid username or password     |
      | validUser |             | Password is required             |
      |           | validPass   | Username is required             |
      |           |             | Username and password are required|

  @security
  Scenario: Account lockout after multiple failed attempts
    When I enter username "validUser"
    And I enter incorrect password 3 times
    Then the account should be temporarily locked
    And I should see error message "Account locked. Please try again in 15 minutes"

  @regression
  Scenario: Password reset functionality
    When I click on forgot password link
    Then I should be redirected to password reset page
    When I enter my email "user@example.com"
    And I click reset password button
    Then I should see message "Password reset instructions sent to your email"