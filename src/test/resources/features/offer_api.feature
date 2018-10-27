Feature: simple merchant offer api

  As a developer
  I want to expose offer service as a rest api
  so that merchants and customers can interact with it at will

  Background:
    Given fresh api is started

  Scenario: request non existing api
    When I send a "GET" request to "/offer/OFFER404"
    Then I should have received 404 status code
