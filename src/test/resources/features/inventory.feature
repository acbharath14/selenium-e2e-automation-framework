@ui
Feature: Product inventory

  Background:
    Given I am signed in as the standard user

  Scenario: Products page lists products with names and prices
    Then every product should have a name and a price

  Scenario Outline: <title>
    When I sort the products by "<sort>"
    Then the products should be listed in "<sort>" order

    Examples:
      | title                                                              | sort              |
      | Sort control orders the products correctly                               | Name (A to Z)     |
      | Sort control orders the products correctly                               | Name (Z to A)     |
      | Sort control orders the products correctly                               | Price (low to high) |
      | Sort control orders the products correctly                               | Price (high to low) |

  Scenario: Adding a product from the products page makes the cart icon show one item
    Given the cart is empty
    When I add "Sauce Labs Backpack" to the cart
    Then the cart icon should show 1 item
