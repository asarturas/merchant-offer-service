package com.spikerlabs.offers

import java.time.LocalDateTime

import cats.effect.IO
import io.circe.java8.time._
import com.spikerlabs.offers.domain.Offer
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.circe.jsonOf
import org.http4s.EntityDecoder

case class OfferResponseBody(products: List[String], price: String, validUntil: LocalDateTime, description: String, code: String)

object OfferResponseBody {

  def fromOffer(offer: Offer): OfferResponseBody = {
    OfferResponseBody(
      offer.articleIds.map(_.value),
      s"£${offer.discount.value.toString}",
      offer.validFor.value,
      offer.description,
      offer.code.value
    )
  }

  implicit val jsonEncoder: Encoder[OfferResponseBody] = deriveEncoder[OfferResponseBody]
  implicit val jsonDecoder: Decoder[OfferResponseBody] = deriveDecoder[OfferResponseBody]
  implicit val entityDecoder: EntityDecoder[IO, OfferResponseBody] = jsonOf[IO, OfferResponseBody]
}