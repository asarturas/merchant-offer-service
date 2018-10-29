import cats.effect.IO
import com.spikerlabs.offers.storage.InMemoryOfferStore
import com.spikerlabs.offers.{OffersApi, OffersService}
import com.spikerlabs.offers.domain.Offer.{utcLocalDateTime, LocalDateTimeProvider}
import org.http4s.{Request, Response}

package object steps {

  case class ApiState(
                       service: OffersService = OffersService.withStore(new InMemoryOfferStore),
                       interactions: List[(Request[IO], Response[IO])] = Nil,
                       customTimer: Option[LocalDateTimeProvider] = None
                     ) {
    val api: OffersApi =
      if (customTimer.isEmpty) OffersApi.forService(service)
      else OffersApi.forService(service)(customTimer.get)
  }

  var apiState = ApiState()

}
