Feature: merchant offer

  As a merchant
  I want to create an offer
  so that I can share it with my customers

  Background:
    Given There is completely fresh data store

  Scenario: create a fixed price offer for a single article
    When I create a fixed price offer:
      | description     | 10 pounds off the single article |
      | target articles | A123                             |
      | discount        | £10.00                           |
      | valid for       | 1 day                            |
    Then I should receive 1 offer for article "A123":
      | description                      | discount | target articles | valid for |
      | 10 pounds off the single article | £10.00   | A123            | 1 day     |

  Scenario: create a fixed price offer for list of articles
    When I create a fixed price offer:
      | description     | 20 pounds off many articles |
      | target articles | A123, B321                  |
      | discount        | £20.00                      |
      | valid for       | 1 day                       |
    Then I should receive 1 offer for article "A123":
      | description                 | discount | target articles | valid for |
      | 20 pounds off many articles | £20.00   | A123, B321      | 1 day     |
    And I should receive 1 offer for article "B321":
      | description                 | discount | target articles | valid for |
      | 20 pounds off many articles | £20.00   | A123, B321      | 1 day     |
