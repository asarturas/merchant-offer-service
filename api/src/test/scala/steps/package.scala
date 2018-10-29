import cats.effect.IO
import com.spikerlabs.offers.storage.InMemoryOfferStore
import com.spikerlabs.offers.{OffersApi, OffersService}
import org.http4s.{Request, Response}
import com.spikerlabs.offers.domain.Offer.utcLocalDateTime

package object steps {

  case class ApiState(
                       api: OffersApi = OffersApi.forService(OffersService.withStore(new InMemoryOfferStore)),
                       interactions: List[(Request[IO], Response[IO])] = Nil
                     )
  var state = ApiState()

}
