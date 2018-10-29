package com.spikerlabs.offers

import cats.effect._
import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import io.circe.syntax._
import io.circe.java8.time._

import scala.language.higherKinds
import scala.util.Success

class OffersApi(service: OffersService)
               (implicit timer: LocalDateTimeProvider) {
  def s: HttpService[IO] = HttpService[IO] {

    case GET -> Root / "offer" / offerCode =>
      service.getOffer(OfferCode(offerCode)) match {
        case None => NotFound()
        case Some(offer) =>
          implicit val foo2Decoder: Encoder[OfferResponseBody] = deriveEncoder[OfferResponseBody]
          Ok(OfferResponseBody.fromOffer(offer).asJson)
      }

    case request@POST -> Root / "offer" =>
      implicit val foo2Decoder: Decoder[OfferRequestBody] = deriveDecoder[OfferRequestBody]
      implicit val decoder = jsonOf[IO, OfferRequestBody]
      request.as[OfferRequestBody].map(_.toOffer).flatMap(addOffer)

  }

  private def addOffer(maybeOffer: Option[Offer]): IO[Response[IO]] = {
    maybeOffer match {
      case Some(offer) =>
        service.addOffer(offer) match {
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