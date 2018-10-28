package steps

import cats.effect.IO
import com.spikerlabs.offers.storage.InMemoryOfferStore
import com.spikerlabs.offers.{OffersApi, OffersService}
import com.spikerlabs.offers.domain.Offer.{utcLocalDateTime, LocalDateTimeProvider}
import cucumber.api.scala.{EN, ScalaDsl}
import org.http4s._
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

  case class OfferApiState(
                            api: OffersApi = OffersApi.forService(OffersService.withStore(new InMemoryOfferStore)),
                            interactions: List[(Request[IO], Response[IO])] = Nil
                          ) extends State

}