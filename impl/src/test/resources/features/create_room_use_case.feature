Feature: As an admin, I want to manage rooms. As a user, I want to be able to see the available rooms

  Scenario: As an admin, I should be able to create a room
    When a room is created
    Then the room should be stored
    And a notification of a created room should be published