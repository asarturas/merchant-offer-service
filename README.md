# Merchant Offers Service

A simple RESTful service that allows a merchant to create a new simple offer.

* In this example only simple fixed price offers are supported;
* Offers have fixed expiration date, expired offers are not returned to consumers;
* Offers can be queried by offer code or by product id (latter returns a list of matching offers);
* Offers can be cancelled;

## Features

* Domain service and RESTful api machinery are explicitly separated via sbt sub-projects boundary;
* Domain is implemented in `service` project, see [feature file](service/src/test/resources/features/offer_service.feature);
* RESTful api is implemented in `api` project, see [feature file](api/src/test/resources/features/offer_api.feature).

## Tests

Verify all unit tests with `sbt test`
Verify all features with `sbt cucumber`
Verify that application as able to start with `bash e2e_test.sh`
Run server for local ad-hoc interactions with `sbt "api/runMain com.spikerlabs.offers.App"`
Fat jar can be built with `sbt "api/assembly"`