Feature: simple merchant offer api

  As a developer
  I want to expose offer service as a rest api
  so that merchants and customers can interact with it at will

  Background:
    Given the api have started on "2018-01-01"

  Scenario: request non existing api
    When I send a "GET" request to "/offer/OFFER404"
    Then I should have received 404 status code

  Scenario: post an offer
    When I send a "POST" request to "/offer":
      """
      {
        "products": ["A123"],
        "price": "£10.00",
        "validFor": "1 day",
        "description": "special price of 10 pounds just today!",
        "code": "OFFER1"
      }
      """
    Then I should have received 201 status code
    And response should contain "Location" header with value "/offer/OFFER1"

  Scenario: get an existing offer by id
    Given I sent a "POST" request to "/offer":
      """
      {
        "products": ["A123"],
        "price": "£10.00",
        "validFor": "1 day",
        "description": "£10 only, buy now!",
        "code": "OFFER1"
      }
      """
    When I send a "GET" request to "/offer/OFFER1"
    Then I should have received 200 status code
    And response body should have been:
      """
      {
        "products": ["A123"],
        "price": "£10",
        "validUntil": "2018-01-02T00:00:00",
        "description": "£10 only, buy now!",
        "code": "OFFER1"
      }
      """

  Scenario: get matching offers by product id
    Given I sent a "POST" request to "/offer":
      """
      {
        "products": ["A123"],
        "price": "£10.00",
        "validFor": "1 day",
        "description": "£10 only, buy now!",
        "code": "OFFER1"
      }
      """
    And I sent a "POST" request to "/offer":
      """
      {
        "products": ["A123"],
        "price": "£20.00",
        "validFor": "2 day",
        "description": "£20 only!",
        "code": "OFFER2"
      }
      """
    When I send a "GET" request to "/productOffers/A123"
    Then I should have received 200 status code
    And response body should have been:
      """
      [
        {
          "products": ["A123"],
          "price": "£10",
          "validUntil": "2018-01-02T00:00:00",
          "description": "£10 only, buy now!",
          "code": "OFFER1"
        },
        {
          "products": ["A123"],
          "price": "£20",
          "validUntil": "2018-01-03T00:00:00",
          "description": "£20 only!",
          "code": "OFFER2"
        }
      ]
      """

  Scenario: cancel an offer
    Given I sent a "POST" request to "/offer":
      """
      {
        "products": ["A123"],
        "price": "£10.00",
        "validFor": "1 day",
        "description": "£10 only, buy now!",
        "code": "OFFER1"
      }
      """
    When I send a "DELETE" request to "/offer/OFFER1"
    Then I should have received 204 status code
    And consequent "GET" request to "/offer/OFFER1" should return 404 status code