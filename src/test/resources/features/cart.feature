@ui
Feature: Cart

  Background:
    Given I am signed in as the standard user

  Scenario: Products added from the products page appear in the cart with correct names and prices
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    And I open the cart
    Then the cart should contain "Sauce Labs Backpack"
    And the cart should contain "Sauce Labs Bike Light"
    And the cart should show the price "$29.99"
    And the cart should show the price "$9.99"

  Scenario: Removing a product from the cart leaves the cart empty
    When I add "Sauce Labs Backpack" to the cart
    And I open the cart
    Then the cart should contain 1 item
    When I remove "Sauce Labs Backpack" from the cart
    Then the cart should be empty
