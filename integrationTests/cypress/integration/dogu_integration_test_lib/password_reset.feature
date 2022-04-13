Feature: Browser-based CAS login and logout functionality

  @requires_new_user
  @requires_testuser
  Scenario: a newly created user with the password reset flag enabled logs himself in and must change his password
    Given the user is member of the admin user group
    When the user opens the user creation page
    And the user opens and fills the form to create a new user
    And the user enables the password reset flag
    And the user clicks save
    And the user logs out by visiting the cas logout page
    And the newly created user logs in
    Then the newly created user is asked to change his password

