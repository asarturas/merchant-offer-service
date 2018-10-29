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

    def lastResponse: Response[IO] = interactions match {
      case Nil => throw new Exception("attempted to get last response when no api interactions are being recorded so far")
      case (_, response) :: _ => response
    }
  }

  var apiState = ApiState()

}
