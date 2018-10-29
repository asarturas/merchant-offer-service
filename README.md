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

* Verify all unit tests with `sbt test`
* Verify all features with `sbt cucumber`
* Verify that application as able to start with `bash e2e_test.sh`
* Run server for local ad-hoc interactions with `sbt "api/runMain com.spikerlabs.offers.App"`
* Fat jar can be built with `sbt "api/assembly"`

## Schemas

Input Offer:
```
{
    "products": ["A123", "B234", "C345"],
    "price": "£19.99",
    "validFor": "2 days",
    "description": "£19.00 only, buy now!",
    "code": "OFFER1"
}
```
Notes: code is optional (will be generated if missing), validFor could be a fixed date instead

Output Offer:
```
{
    "products": ["A123", "B234", "C345"],
    "price": "£19.99",
    "validUntil": "2018-01-02T00:00:00",
    "description": "£19.99 only, buy now!",
    "code": "OFFER1"
}
```

## Examples

### Post a new offer
```
curl http://localhost:8080/offer --data '{"products":["A123"],"price":"£19.99","validFor":"1 day","description":"buy now!!!"}' --verbose
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> POST /offer HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
> Content-Length: 85
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 85 out of 85 bytes
< HTTP/1.1 201 Created
< Location: /offer/c63ea8a8-1411-46a6-b97e-1854fbbd018c
< Date: Mon, 29 Oct 2018 23:34:46 GMT
< Content-Length: 0
```

### Get an offer by ID
```
curl http://localhost:8080/offer/c63ea8a8-1411-46a6-b97e-1854fbbd018c --verbose
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /offer/c63ea8a8-1411-46a6-b97e-1854fbbd018c HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Content-Type: application/json
< Date: Mon, 29 Oct 2018 23:35:46 GMT
< Content-Length: 151
<
* Connection #0 to host localhost left intact
{"products":["A123"],"price":"£19.99","validUntil":"2018-10-30T23:34:46.286","description":"buy now!!!","code":"c63ea8a8-1411-46a6-b97e-1854fbbd018c"}
```

### Get matching offers by Product ID
```
curl http://localhost:8080/productOffers/A123 --verbose
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /productOffers/A123 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Content-Type: application/json
< Date: Mon, 29 Oct 2018 23:36:52 GMT
< Content-Length: 305
<
* Connection #0 to host localhost left intact
[{"products":["A123"],"price":"£19.99","validUntil":"2018-10-30T23:34:46.286","description":"buy now!!!","code":"c63ea8a8-1411-46a6-b97e-1854fbbd018c"}]
```

### Cancel offer
```
curl -XDELETE http://localhost:8080/offer/OFFER1 --verbose
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> DELETE /offer/OFFER1 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 204 No Content
< Date: Mon, 29 Oct 2018 23:39:44 GMT
```

### Get missing or expired offer
```
curl http://localhost:8080/offer/X --verbose
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /offer/X HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 404 Not Found
< Date: Mon, 29 Oct 2018 23:38:39 GMT
< Content-Length: 0
```

### Example response when application fails unexpectedly
```
curl "http://localhost:8080/offer/3f199263-f5d1-4d2d-81d6-21b67312f0f9" --verbose
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /offer/3f199263-f5d1-4d2d-81d6-21b67312f0f9 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 500 Internal Server Error
< Connection: close
< Date: Mon, 29 Oct 2018 23:29:27 GMT
< Content-Length: 0
```

## Limitations

* The end to end test is very minimal;
* The service is hardcoded to IO, could be generalised;
* There is no cleanup of expired offers and no way for consumers to see expired offers;
* Matching product offers could be ordered by expiration date;
* Offer currency is hardcoded and BigDecimal is used instead of just using currency;
* Offer validity range is limited to integer days;
* All dates are in UTC (but it does not explicitly say so in output);
