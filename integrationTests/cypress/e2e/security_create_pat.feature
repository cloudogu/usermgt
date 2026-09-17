@created_pats
Feature: Personal access tokens on the security page

  Background:
    Given the user "nomanager" exists
    And the user "nomanager" with password "newuserpassword1234A$" is logged in
    When the user opens the security page

  Scenario: Create PATs for all dogus and for usermgt
    Given the user remembers the current PAT count
    When the user creates a PAT named "all-dogus" for "Alle Dogus"
    And the user creates a PAT named "usermgt-only" for "/usermgt"
    Then the PAT overview contains the PAT named "all-dogus"
    And the PAT overview contains the PAT named "usermgt-only"
    And the PAT count has increased by 2

  Scenario: Use a usermgt PAT to retrieve users
    When the user creates a PAT named "usermgt-api" for "/usermgt"
    Then the user can retrieve users with the PAT named "usermgt-api"

  @delete_pat_from_table
  Scenario: Delete a PAT from the overview table
    Given the user remembers the current PAT count
    When the user creates a PAT named "table-delete" for "/usermgt"
    Then the PAT count has increased by 1
    When the user deletes the PAT named "table-delete" using the table action
    Then the PAT count matches the remembered PAT count

  @delete_pat_from_details
  Scenario: Delete a PAT from its detail page
    Given the user remembers the current PAT count
    When the user creates a PAT named "details-delete" for "/usermgt"
    Then the PAT count has increased by 1
    When the user opens the details of the PAT named "details-delete"
    And the user deletes the PAT from the detail page
    Then the PAT count matches the remembered PAT count

  Scenario Outline: Reject invalid PAT names
    When the user opens the PAT creation form
    And the user enters the PAT validation name "<name>"
    And the user selects the PAT expiry option "7"
    And the user selects the PAT scope "/usermgt"
    And the user submits the PAT validation form
    Then the PAT name error is "<error>"
    And no PAT creation request was sent

    Examples:
      | name               | error                                                                                              |
      | empty              | Please enter a name.                                                                               |
      | 65 characters      | The name must not exceed 64 characters.                                                            |
      | embedded space     | The name contains unsupported characters. Remove whitespace or other invisible special characters. |
      | leading space      | The name contains unsupported characters. Remove whitespace or other invisible special characters. |
      | trailing space     | The name contains unsupported characters. Remove whitespace or other invisible special characters. |
      | only spaces        | Please enter a name.                                                                               |
      | non-breaking space | The name contains unsupported characters. Remove whitespace or other invisible special characters. |
      | zero-width space   | The name contains unsupported characters. Remove whitespace or other invisible special characters. |

  Scenario: Reject the name of an existing key
    When the user creates a PAT named "predefined-validation-key" for "/usermgt"
    And the user opens the PAT creation form
    And the user enters the existing PAT name "predefined-validation-key"
    And the user selects the PAT expiry option "7"
    And the user selects the PAT scope "/usermgt"
    And the user submits the PAT validation form
    Then the PAT name error is "This name is already in use. Choose another name."
    And no PAT creation request was sent

  Scenario: Require an explicit expiry selection but allow no expiration
    When the user opens the PAT creation form
    And the user enters the PAT validation name "valid"
    And the user selects the PAT scope "/usermgt"
    And the user submits the PAT validation form
    Then the PAT validation message "Please select a validity period." is visible
    And no PAT creation request was sent
    When the user selects the PAT expiry option "0"
    And the user submits the PAT validation form
    Then the validation PAT is created without expiration

  Scenario: Require at least one dogu
    When the user opens the PAT creation form
    And the user enters the PAT validation name "valid"
    And the user selects the PAT expiry option "7"
    And the user submits the PAT validation form
    Then the PAT dogu selection is invalid
    And the PAT validation message "Select the Dogus for which the key is valid" is visible
    And no PAT creation request was sent
    When the user selects the PAT scope "/usermgt"
    And the user submits the PAT validation form
    Then the validation PAT is created

  Scenario Outline: Accept valid boundary names and visible special characters
    When the user opens the PAT creation form
    And the user enters the PAT validation name "<name>"
    And the user selects the PAT expiry option "7"
    And the user selects the PAT scope "/usermgt"
    And the user submits the PAT validation form
    Then the validation PAT is created

    Examples:
      | name                       |
      | 64 characters              |
      | visible special characters |
