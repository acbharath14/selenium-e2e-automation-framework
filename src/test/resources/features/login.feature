@ui
Feature: Login

  Scenario Outline: <title>
    Given I am on the Swag Labs login page
    When I sign in as "<user>"
    Then <outcome>

    Examples:
      | title                                                                          | user               | outcome                                           |
      | Valid users sign in successfully and see the products page                | standard           | I should see the products page                    |
      | Locked-out user is blocked and sees the locked-out message                     | locked-out         | I should see the locked-out message               |
      | Problem user signs in and sees the products page with broken product images    | problem            | I should see the products page with broken images |
      | Valid users sign in successfully and see the products page                | performance glitch | I should see the products page                    |
      | Wrong username or password is rejected with an error message                   | invalid            | I should see a credentials mismatch error         |
