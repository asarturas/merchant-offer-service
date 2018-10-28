package steps

import cats.effect.IO
import cucumber.api.PendingException
import org.http4s._
import org.http4s.Headers
import org.http4s.dsl.io._
import org.scalatest.AppendedClues
import steps.StatefulSteps.OfferApiState
import fs2.Stream
import fs2.text.utf8Encode

class OffersApiSteps extends StatefulSteps[OfferApiState] with AppendedClues {

  var state: OfferApiState = OfferApiState()

  Given("""^fresh api is started$""") { () =>
    state = OfferApiState()
  }

  When("""^I send a "([^"]*)" request to "([^"]*)"$""") { (methodName: String, relativeUri: String) =>
    val method = methodName match {
      case "GET" => Method.GET
    }
    val request: Request[IO] = Request(method = method, uri = Uri.unsafeFromString(relativeUri))
    val response = state.api.s.orNotFound.run(request).unsafeRunSync
    state = state.copy(interactions = (request, response) :: state.interactions)
  }

  When("""^I send a "([^"]*)" request to "([^"]*)":$""") { (methodName: String, relativeUri: String, body: String) =>
    val method = methodName match {
      case "POST" => Method.POST
    }
    val b: EntityBody[IO] = Stream(body).throughPure(utf8Encode)
    val request: Request[IO] = Request(method = method, uri = Uri.unsafeFromString(relativeUri), body = b)
    val response = state.api.s.orNotFound.run(request).unsafeRunSync
    state = state.copy(interactions = (request, response) :: state.interactions)
  }

  Then("""^response should contain "([^"]*)" header with value "([^"]*)"$""") { (arg0: String, arg1: String) =>
    val (_, response: Response[IO]) = state.interactions.head
    response.headers should contain (Header(arg0, arg1))
  }

  Then("""^I should have received (\d+) status code$""") { (arg0: Int) =>
    val expectedCode = arg0 match {
      case 404 => Status.NotFound
      case 201 => Status.Created
      case 200 => Status.Ok
    }
    val (_: Request[IO], response: Response[IO]) = state.interactions.head
    response.status shouldBe expectedCode withClue(response, state.interactions)
    response.body.compile.toVector.unsafeRunSync should be(empty) withClue(response, state.interactions)
  }

}
