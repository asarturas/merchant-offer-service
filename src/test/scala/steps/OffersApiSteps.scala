package steps

import cats.effect.IO
import cucumber.api.PendingException
import org.http4s._
import org.http4s.dsl.io._
import org.scalatest.AppendedClues
import steps.StatefulSteps.OfferApiState

class OffersApiSteps extends StatefulSteps[OfferApiState] with AppendedClues {

  var state: OfferApiState = OfferApiState()

  Given("""^fresh api is started$""") { () =>
    state = OfferApiState()
  }

  When("""^I send a "([^"]*)" request to "([^"]*)"$""") { (arg0: String, arg1: String) =>
    val method = arg0 match {
      case "GET" => Method.GET
    }
    val request: Request[IO] = Request(method = method, uri = Uri.unsafeFromString(arg1))
    val response = state.api.service.orNotFound.run(request).unsafeRunSync
    state = state.copy(interactions = (request, response) :: state.interactions)
  }

  Then("""^I should have received (\d+) status code$""") { (arg0: Int) =>
    val expectedCode =
      if (arg0 == 404) Status.NotFound
      else Status.Ok
    val (_: Request[IO], response: Response[IO]) = state.interactions.head
    response.status shouldBe expectedCode withClue(response, state.interactions)
    response.body.compile.toVector.unsafeRunSync should be(empty) withClue(response, state.interactions)
  }

}
