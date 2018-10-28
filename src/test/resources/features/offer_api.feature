Feature: simple merchant offer api

  As a developer
  I want to expose offer service as a rest api
  so that merchants and customers can interact with it at will

  Background:
    Given fresh api is started

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