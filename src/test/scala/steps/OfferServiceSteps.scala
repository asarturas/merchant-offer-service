package steps

import cucumber.api.{DataTable, PendingException}
import steps.StatefulSteps.OfferServiceState

class OfferServiceSteps extends StatefulSteps[OfferServiceState] {
  var state: OfferServiceState = OfferServiceState()

  When("""^I create a fixed price offer:$""") { (offerInformation: DataTable) =>
      throw new PendingException()
  }
  Then("""^I should receive (\d+) offer for article "([^"]*)":$""") { (expectedNumberOfOffers: Int, articleId: String, expectedOffers: DataTable) =>
      throw new PendingException()
  }

}
