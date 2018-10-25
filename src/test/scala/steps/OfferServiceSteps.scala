package steps

import java.util

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{ArticleId, Discount, ValidFor}
import cucumber.api.{DataTable, PendingException}
import steps.StatefulSteps.OfferServiceState

import collection.JavaConverters._

class OfferServiceSteps extends StatefulSteps[OfferServiceState] with Transformers {
  var state: OfferServiceState = OfferServiceState()

  When("""^I create a fixed price offer:$""") { (singleOfferTable: DataTable) =>
    val offer = singleOfferTableToOffer(singleOfferTable)
    state.store.store(offer)
  }
  Then("""^I should receive (\d+) offer for article "([^"]*)":$""") { (expectedNumberOfOffers: Int, articleId: String, expectedOffersTable: DataTable) =>
    val matchingOffers = state.store.getOffers(ArticleId(articleId))
    matchingOffers should have size expectedNumberOfOffers

    val expectedOffers = multipleOffersTableToOffers(expectedOffersTable)
    matchingOffers should contain allElementsOf expectedOffers
  }

}
