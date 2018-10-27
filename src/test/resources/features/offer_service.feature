Feature: simple merchant offer for products at a special price

  As a merchant
  I want to create an offer
  so that I can share it with my customers

  Background:
    Given There is completely fresh data store
    And it is midnight of "2018-01-01"

  Scenario: create a fixed price offer for a single product
    When I create a fixed price offer:
      | products    | A123                                   |
      | price       | £10.00                                 |
      | valid for   | 1 day                                  |
      | description | special price of 10 pounds just today! |
    Then I should receive 1 offer for product "A123":
      | price  | products | valid for | description                            |
      | £10.00 | A123     | 1 day     | special price of 10 pounds just today! |

  Scenario: create a fixed price offer for list of products
    When I create a fixed price offer:
      | products    | A123, B321                           |
      | price       | £20.00                               |
      | valid for   | 1 day                                |
      | description | these are for 20 pounds for one day! |
    Then I should receive 1 offer for product "A123":
      | price  | products   | valid for | description                          |
      | £20.00 | A123, B321 | 1 day     | these are for 20 pounds for one day! |
    And I should receive 1 offer for product "B321":
      | price  | products   | valid for | description                          |
      | £20.00 | A123, B321 | 1 day     | these are for 20 pounds for one day! |

  Scenario: do not return expired offers
    When there are number of offers available:
      | price  | products   | valid for | description        |
      | £10.00 | A123, B234 | 1 day     | £10 only, buy now! |
      | £20.00 | A123, C345 | 3 days    | £20 only, buy now! |
      | £30.00 | A123, D456 | 5 days    | £30 only, buy now! |
    And 2 days have passed
    Then I should receive 2 offers for product "A123":
      | price  | products   | valid until | description        |
      | £20.00 | A123, C345 | 2018-01-04  | £20 only, buy now! |
      | £30.00 | A123, D456 | 2018-01-06  | £30 only, buy now! |
