Feature: merchant offer

  As a merchant
  I want to create an offer
  so that I can share it with my customers

  Scenario: create a fixed price offer for a single article
    When I create a fixed price offer:
      | description    | 10 pounds off the single article |
      | target article | A123                             |
      | discount       | £10.00                           |
      | expires in     | 1 day                            |
    Then I should receive 1 offer for article "A123":
      | description                      | discount | expires in |
      | 10 pounds off the single article | £10.00   | 1 day      |
