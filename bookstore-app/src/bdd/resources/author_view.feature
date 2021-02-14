Feature: Author view panel
  Specification of the behaviour of the Author View panel

  Scenario: The initial state of the view
    Given The database contains the authors with the following values
      | Someone |
      | Another |
    When The application starts
    And The "Authors" view is shown
    Then The list contains elements with the following values
      | 1 | Someone |
      | 2 | Another |

  Scenario: Add a new author
    Given The application starts
    And The "Authors" view is shown
    When The user enter "a new author" in the text field
    And The user click the "Add" button
    Then The list contains elements with the following values
      | 1 | a new author |
