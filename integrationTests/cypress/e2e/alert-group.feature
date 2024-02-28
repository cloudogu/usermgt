Feature: Tests that verify that an appropriate toast is shown when creating, editing or deleting groups

  Background:
    Given the user "manager" exists
    And "1" test-groups exist
    And the user "manager" is member of the group "cesManager"
    And the user opens the groups page

  @clean_new_group
  Scenario: toast on group creation is shown
    When the user clicks on the create-group button
    And the new-group-page is shown
    When the user fills the group-form for a group with the name "testGroup_new"
    And the user submits the group-form
    Then a success alert will be shown containing the text "testGroup_new"

  Scenario: toast on group editing is shown
    When the user clicks on the edit-group button for the group "testGroup_1"
    And the edit-group-page for group "testGroup_1" is shown
    When the user edits the group-description to "Better description"
    And the user submits the group-form
    Then a success alert will be shown containing the text "The group 'testGroup_1' was saved successfully."

  @reduce_group_env
  Scenario: toast on group deletion is shown
    When the user sets the groups-filter to "testGroup_1"
    When the user clicks on the delete-group button for the group "testGroup_1"
    And the user confirms the delete-group-confirmation-dialog
    Then a success alert will be shown containing the text "The group 'testGroup_1' was deleted successfully."
