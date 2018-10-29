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
    val method = methodName match {
      case "GET" => Method.GET
    }
    val request: Request[IO] = Request(method = method, uri = Uri.unsafeFromString(relativeUri))
    val response = apiState.api.service.orNotFound.run(request).unsafeRunSync
    apiState = apiState.copy(interactions = (request, response) :: apiState.interactions)
  }

  When("""^I sen[dt] a "([^"]*)" request to "([^"]*)":$""") { (methodName: String, relativeUri: String, bodyValue: String) =>
    val method = methodName match {
      case "POST" => Method.POST
    }
    val body: EntityBody[IO] = Stream(bodyValue).throughPure(utf8Encode)
    val request: Request[IO] = Request(method = method, uri = Uri.unsafeFromString(relativeUri), body = body)
    val response = apiState.api.service.orNotFound.run(request).unsafeRunSync
    apiState = apiState.copy(interactions = (request, response) :: apiState.interactions)
  }

  Then("""^I should have received (\d+) status code$""") { (expectedStatusCode: Int) =>
    val expectedCode = expectedStatusCode match {
      case 404 => Status.NotFound
      case 201 => Status.Created
      case 200 => Status.Ok
    }
    apiState.lastResponse.status shouldBe expectedCode withClue apiState.interactions
  }

  Then("""^response should contain "([^"]*)" header with value "([^"]*)"$""") { (expectedHeaderName: String, expectedHeaderValue: String) =>
    apiState.lastResponse.headers should contain(Header(expectedHeaderName, expectedHeaderValue))
  }

  Then("""^response body should have been:$""") { (expectedBodyValue: String) =>
    val actualBodyValue = apiState.lastResponse.bodyAsText.compile.toVector.unsafeRunSync().mkString("")
    val expectedBody = decode[OfferResponseBody](expectedBodyValue)
    expectedBody.isRight shouldBe true withClue expectedBody
    val actualBody = decode[OfferResponseBody](actualBodyValue)
    actualBody.isRight shouldBe true withClue actualBody
    actualBody shouldBe expectedBody
  }

}