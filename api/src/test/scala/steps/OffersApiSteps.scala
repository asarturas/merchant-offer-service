package steps

import java.time.LocalDateTime

import cats.effect.IO
import com.spikerlabs.offers.domain.Offer.LocalDateTimeProvider
import com.spikerlabs.offers.OfferResponseBody
import cucumber.api.scala.{EN, ScalaDsl}
import fs2.Stream
import fs2.text.utf8Encode
import org.http4s._
import org.http4s.dsl.io._
import org.scalatest.{AppendedClues, Matchers}
import io.circe.parser.decode

class OffersApiSteps extends ScalaDsl with EN with Matchers with AppendedClues {

  Given("""^the api have started on "([^"]*)"$""") { (date: String) =>
    val localDate = LocalDateTime.parse(s"${date}T00:00:00")
    implicit val timer: LocalDateTimeProvider = () => localDate
    apiState = apiState.copy(
      service = apiState.service.copy()(timer),
      customTimer = Some(timer)
    )
  }

  When("""^I send a "([^"]*)" request to "([^"]*)"$""") { (methodName: String, relativeUri: String) =>
    val request: Request[IO] = Request(method = httpMethod(methodName), uri = Uri.unsafeFromString(relativeUri))
    val response = apiState.api.httpService.orNotFound.run(request).unsafeRunSync
    apiState = apiState.copy(interactions = (request, response) :: apiState.interactions)
  }

  When("""^I sen[dt] a "([^"]*)" request to "([^"]*)":$""") { (methodName: String, relativeUri: String, bodyValue: String) =>
    val body: EntityBody[IO] = Stream(bodyValue).throughPure(utf8Encode)
    val request: Request[IO] = Request(method = httpMethod(methodName), uri = Uri.unsafeFromString(relativeUri), body = body)
    val response = apiState.api.httpService.orNotFound.run(request).unsafeRunSync
    apiState = apiState.copy(interactions = (request, response) :: apiState.interactions)
  }

  Then("""^I should have received (\d+) status code$""") { (expectedStatusCode: Int) =>
    apiState.lastResponse.status shouldBe httpStatus(expectedStatusCode) withClue apiState.interactions
  }

  Then("""^response should contain "([^"]*)" header with value "([^"]*)"$""") { (expectedHeaderName: String, expectedHeaderValue: String) =>
    apiState.lastResponse.headers should contain(Header(expectedHeaderName, expectedHeaderValue))
  }

  Then("""^response body should have been:$""") { (expectedBodyValue: String) =>
    val expectingAList = expectedBodyValue.trim.head == '['
    val actualBodyValue = apiState.lastResponse.bodyAsText.compile.toVector.unsafeRunSync().mkString("")
    if (expectingAList) {
      val expectedBody = decode[List[OfferResponseBody]](expectedBodyValue)
      val actualBody = decode[List[OfferResponseBody]](actualBodyValue)
      expectedBody.isRight shouldBe true withClue expectedBody
      actualBody.isRight shouldBe true withClue actualBody
      actualBody.right.get should contain allElementsOf expectedBody.right.get
    } else {
      val expectedBody = decode[OfferResponseBody](expectedBodyValue)
      val actualBody = decode[OfferResponseBody](actualBodyValue)
      expectedBody.isRight shouldBe true withClue expectedBody
      actualBody.isRight shouldBe true withClue actualBody
      actualBody shouldBe expectedBody
    }
  }

  Then("""^consequent "([^"]*)" request to "([^"]*)" should return (\d+) status code$""") { (methodName: String, relativeUri: String, expectedCode: Int) =>
    val request: Request[IO] = Request(method = httpMethod(methodName), uri = Uri.unsafeFromString(relativeUri))
    val response = apiState.api.httpService.orNotFound.run(request).unsafeRunSync
    apiState = apiState.copy(interactions = (request, response) :: apiState.interactions)
    apiState.lastResponse.status shouldBe httpStatus(expectedCode) withClue apiState.interactions
  }

  private def httpMethod(methodName: String) = methodName match {
    case "GET" => Method.GET
    case "POST" => Method.POST
    case "DELETE" => Method.DELETE
  }

  private def httpStatus(statusCode: Int) = statusCode match {
    case 404 => Status.NotFound
    case 201 => Status.Created
    case 204 => Status.NoContent
    case 200 => Status.Ok
  }

}