@ui
Feature: Checkout

  Background:
    Given I am signed in as the standard user
    And I add the checkout items to the cart
    And I open the cart
    And I start checkout
    And I fill in the checkout information

  Scenario: Checkout summary shows $39.98 subtotal, $3.20 tax, and $43.18 total before the order is placed
    Then the order summary should show "Item total: $39.98", "Tax: $3.20", and "Total: $43.18"

  Scenario: Completing checkout with valid details shows the order confirmation page
    When I finish the order
    Then I should see the order confirmation page
