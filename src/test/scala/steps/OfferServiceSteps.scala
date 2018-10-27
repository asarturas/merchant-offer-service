package steps

import java.time.LocalDateTime

import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferId, Product}
import cucumber.api.DataTable
import org.scalatest.AppendedClues
import steps.StatefulSteps.OfferServiceState

class OfferServiceSteps extends StatefulSteps[OfferServiceState] with Transformers with AppendedClues {
  var state: OfferServiceState = OfferServiceState()

  Given("""^There is completely fresh data store$""") { () =>
    state = state.customTimer match {
      case Some(timer) =>
        implicit val customTimer: LocalDateTimeProvider = timer
        OfferServiceState(customTimer = Some(customTimer))
      case None =>
        OfferServiceState()
    }
  }


  Given("""^it is midnight of "([^"]*)"$""") { (date: String) =>
    implicit val staticTimer: LocalDateTimeProvider = () => LocalDateTime.parse(s"${date}T00:00:00")
    state = state.copy(
      service = state.service.copy()(timer = staticTimer),
      customTimer = Some(staticTimer)
    )
  }

  When("""^(\d+) days have passed$""") { (numberOfDays: Int) =>
    if (state.customTimer.isEmpty) throw new Exception(s"Expected to have a custom timer to update, but none available on $state")
    val updatedTime = state.customTimer.get().plusDays(numberOfDays)
    implicit val updatedTimer: LocalDateTimeProvider = () => updatedTime
    state = state.copy(
      service = state.service.copy()(timer = updatedTimer),
      customTimer = Some(updatedTimer)
    )
  }

  When("""^I create a fixed price offer:$""") { (singleOfferTable: DataTable) =>
    val offer = singleOfferTableToOffer(singleOfferTable)(state.customTimer.get)
    state.service.addOffer(offer)
  }

  When("""^there are number of offers available:$""") { (availableOffers: DataTable) =>
    multipleOffersTableToOffers(availableOffers)(state.customTimer.get).foreach(state.service.addOffer)
  }

  Then("""^I should receive (\d+) offers? for product "([^"]*)":$""") { (expectedNumberOfOffers: Int, articleId: String, expectedOffersTable: DataTable) =>
    val matchingOffers = state.service.getOffers(Product(articleId))
    val expectedOffers = multipleOffersTableToOffers(expectedOffersTable)(state.customTimer.get)
    matchingOffers should have size expectedNumberOfOffers withClue expectedOffers
    matchingOffers should contain allElementsOf expectedOffers
  }

  Then("""^I should receive an offer for id "([^"]*)":$""") { (offerCode: String, offerTable: DataTable) =>
    val matchingOffer = state.service.getOffer(OfferId(offerCode))
    matchingOffer should not be None
    matchingOffer.get shouldBe singleOfferTableToOffer(offerTable)(state.customTimer.get)
  }

  Then("""^I should receive no offers for id "([^"]*)"$""") { (offerCode: String) =>
    state.service.getOffer(OfferId(offerCode)) shouldBe None
  }

}
