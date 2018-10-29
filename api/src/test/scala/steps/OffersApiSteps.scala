package steps

import java.time.LocalDateTime

import cats.effect.IO
import com.spikerlabs.offers.domain.Offer.LocalDateTimeProvider
import com.spikerlabs.offers.OffersApi.{OfferBody, OfferResponse}
import com.spikerlabs.offers.OffersService
import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.DataTable
import fs2.Stream
import fs2.text.utf8Encode
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s._
import org.http4s.dsl.io._
import org.scalatest.{AppendedClues, Matchers}
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.java8.time._

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
    val response = apiState.api.s.orNotFound.run(request).unsafeRunSync
    apiState = apiState.copy(interactions = (request, response) :: apiState.interactions)
  }

  When("""^I sen[dt] a "([^"]*)" request to "([^"]*)":$""") { (methodName: String, relativeUri: String, body: String) =>
    val method = methodName match {
      case "POST" => Method.POST
    }
    val b: EntityBody[IO] = Stream(body).throughPure(utf8Encode)
    val request: Request[IO] = Request(method = method, uri = Uri.unsafeFromString(relativeUri), body = b)
    val response = apiState.api.s.orNotFound.run(request).unsafeRunSync
    apiState = apiState.copy(interactions = (request, response) :: apiState.interactions)
  }

  Then("""^I should have received (\d+) status code$""") { (arg0: Int) =>
    val expectedCode = arg0 match {
      case 404 => Status.NotFound
      case 201 => Status.Created
      case 200 => Status.Ok
    }
    val (_: Request[IO], response: Response[IO]) = apiState.interactions.head
    response.status shouldBe expectedCode withClue(response, apiState.interactions)
  }

  Then("""^response should contain "([^"]*)" header with value "([^"]*)"$""") { (arg0: String, arg1: String) =>
    val (_, response: Response[IO]) = apiState.interactions.head
    response.headers should contain(Header(arg0, arg1))
  }

  Then("""^response body should have been:$""") { (arg0: String) =>
    val (_, response: Response[IO]) = apiState.interactions.head
    val body = response.bodyAsText.compile.toVector.unsafeRunSync().mkString("")
    implicit val foo2Decoder: Decoder[OfferResponse] = deriveDecoder[OfferResponse]
    val expected = decode[OfferResponse](arg0)
    val actual = decode[OfferResponse](body)
    expected.isRight shouldBe true withClue expected
    actual.isRight shouldBe true withClue actual
    actual shouldBe expected
  }

}