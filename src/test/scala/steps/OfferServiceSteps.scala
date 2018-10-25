package steps

import com.spikerlabs.offers.domain.Offer.ArticleId
import cucumber.api.DataTable
import steps.StatefulSteps.OfferServiceState

class OfferServiceSteps extends StatefulSteps[OfferServiceState] with Transformers {
  var state: OfferServiceState = _

  Given("""^There is completely fresh data store$""") { () =>
    state = OfferServiceState()
  }

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
