package steps

import com.spikerlabs.offers.storage.InMemoryOfferStore
import com.spikerlabs.offers.OffersService
import com.spikerlabs.offers.domain.Offer.{utcLocalDateTime, LocalDateTimeProvider}
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers

trait StatefulSteps[A] extends ScalaDsl with EN with Matchers {
  var state: A
}

object StatefulSteps {
  trait State
  case class OfferServiceState(
    service: OffersService = OffersService.withStore(new InMemoryOfferStore),
    customTimer: Option[LocalDateTimeProvider] = None
  ) extends State
}