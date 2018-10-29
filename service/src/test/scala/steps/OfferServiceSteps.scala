package steps

import java.time.LocalDateTime

import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode, Product}
import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.{AppendedClues, Matchers}

class OfferServiceSteps extends ScalaDsl with EN with Matchers with Transformers with AppendedClues {

  Given("""^There is completely fresh data store$""") { () =>
    serviceState = serviceState.customTimer match {
      case Some(timer) =>
        implicit val customTimer: LocalDateTimeProvider = timer
        ServiceState(customTimer = Some(customTimer))
      case None =>
        ServiceState()
    }
  }

  Given("""^it is midnight of "([^"]*)"$""") { (date: String) =>
    implicit val staticTimer: LocalDateTimeProvider = () => LocalDateTime.parse(s"${date}T00:00:00")
    serviceState = serviceState.copy(
      service = serviceState.service.copy()(timer = staticTimer),
      customTimer = Some(staticTimer)
    )
  }

  When("""^(\d+) days have passed$""") { (numberOfDays: Int) =>
    if (serviceState.customTimer.isEmpty) throw new Exception(s"Expected to have a custom timer to update, but none available on $serviceState")
    val updatedTime = serviceState.customTimer.get().plusDays(numberOfDays)
    implicit val updatedTimer: LocalDateTimeProvider = () => updatedTime
    serviceState = serviceState.copy(
      service = serviceState.service.copy()(timer = updatedTimer),
      customTimer = Some(updatedTimer)
    )
  }

  When("""^I create a fixed price offer:$""") { (singleOfferTable: DataTable) =>
    val offer = singleOfferTableToOffer(singleOfferTable)(serviceState.customTimer.get)
    serviceState.service.addOffer(offer)
  }

  When("""^I cancel the offer "([^"]*)"$""") { (offer: String) =>
    serviceState.service.cancelOffer(OfferCode(offer))
  }

  When("""^there are number of offers available:$""") { (availableOffers: DataTable) =>
    multipleOffersTableToOffers(availableOffers)(serviceState.customTimer.get).foreach(serviceState.service.addOffer)
  }

  Then("""^I should receive (\d+) offers? for product "([^"]*)":$""") { (expectedNumberOfOffers: Int, articleId: String, expectedOffersTable: DataTable) =>
    val matchingOffers = serviceState.service.getOffers(Product(articleId))
    val expectedOffers = multipleOffersTableToOffers(expectedOffersTable)(serviceState.customTimer.get)
    matchingOffers should have size expectedNumberOfOffers withClue expectedOffers
    matchingOffers should contain allElementsOf expectedOffers
  }

  Then("""^I should receive an offer for code "([^"]*)":$""") { (offerCode: String, offerTable: DataTable) =>
    val matchingOffer = serviceState.service.getOffer(OfferCode(offerCode))
    matchingOffer should not be None
    matchingOffer.get shouldBe singleOfferTableToOffer(offerTable)(serviceState.customTimer.get)
  }

  Then("""^I should receive no offers for code "([^"]*)"$""") { (offerCode: String) =>
    serviceState.service.getOffer(OfferCode(offerCode)) shouldBe None
  }

}
