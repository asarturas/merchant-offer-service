package com.spikerlabs.offers

import cats.effect._
import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import io.circe.syntax._
import org.http4s.server.blaze.BlazeBuilder

import scala.language.higherKinds
import scala.util.Success

class OffersApi(offersService: OffersService)
               (implicit timer: LocalDateTimeProvider) {

  def httpStream(port: Int = 8080, host: String = "0.0.0.0"): BlazeBuilder[IO] =
    BlazeBuilder[IO].bindHttp(port, host).mountService(httpService, "/")

  def httpService: HttpService[IO] = HttpService[IO] {

    case GET -> Root / "offer" / offerCode =>
      offersService.getOffer(OfferCode(offerCode)) match {
        case None => NotFound()
        case Some(offer) =>
          Ok(OfferResponseBody.fromOffer(offer).asJson)
      }

    case GET -> Root / "productOffers" / productId =>
      Ok(offersService.getOffers(Offer.Product(productId)).map(OfferResponseBody.fromOffer).asJson)

    case request@POST -> Root / "offer" =>
      request.as[OfferRequestBody].map(_.toOffer).flatMap(addOffer)

    case DELETE -> Root / "offer" / offerCode =>
      offersService.cancelOffer(OfferCode(offerCode)) match {
        case Success(_) => NoContent()
        case _ => InternalServerError()
      }

    case GET -> Root / "heartbeat" => Ok("ok")

  }

  private def addOffer(maybeOffer: Option[Offer]): IO[Response[IO]] = {
    maybeOffer match {
      case Some(offer) =>
        offersService.addOffer(offer) match {
          case Success(_) => Created().map(_.putHeaders(Header("Location", s"/offer/${offer.code.value}")))
          case _ => InternalServerError()
        }
      case None => InternalServerError()
    }
  }

}

object OffersApi {
  def forService(service: OffersService)
                (implicit timer: LocalDateTimeProvider): OffersApi = new OffersApi(service)
}