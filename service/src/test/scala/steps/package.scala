import com.spikerlabs.offers.domain.Offer.LocalDateTimeProvider
import com.spikerlabs.offers.OffersService
import com.spikerlabs.offers.storage.InMemoryOfferStore
import com.spikerlabs.offers.domain.Offer.utcLocalDateTime

package object steps {
  case class ServiceState(
                         service: OffersService = OffersService.withStore(new InMemoryOfferStore),
                         customTimer: Option[LocalDateTimeProvider] = None
                       )
  var serviceState = ServiceState()
}
