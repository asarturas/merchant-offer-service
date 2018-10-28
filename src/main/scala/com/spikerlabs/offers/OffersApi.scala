package com.spikerlabs.offers

import java.time.LocalDateTime

import cats.effect._
import cats.Monad
import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode, SpecialPrice, ValidUntil}
import com.spikerlabs.offers.OffersApi.OfferBody
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.Decoder
import io.circe._
import io.circe.generic.semiauto._
import io.circe.java8.time._

import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

class OffersApi(service: OffersService)(implicit timer: LocalDateTimeProvider) {
  def s: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "offer" / _ =>
      NotFound()
    case request @ POST -> Root / "offer" =>
      implicit val foo2Decoder: Decoder[OfferBody] = deriveDecoder[OfferBody]
      implicit val decoder = jsonOf[IO, OfferBody]
      request.as[OfferBody].map(_.toOffer).flatMap(addOffer)
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

  case class OfferBody(products: List[String], price: String, validFor: String, description: String, code: Option[String] = None) {
    def toOffer(implicit timer: LocalDateTimeProvider): Option[Offer] = {
      val listOfProducts = products.mkString(", ")
      val offerCode = code.getOrElse("")
      Offer.fromStrings(description, listOfProducts, price, validFor, offerCode)
    }
  }
}