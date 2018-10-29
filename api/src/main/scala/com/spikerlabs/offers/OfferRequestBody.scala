package com.spikerlabs.offers

import cats.effect.IO
import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.LocalDateTimeProvider
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.jsonOf
import org.http4s.EntityDecoder

case class OfferRequestBody(products: List[String], price: String, validFor: String, description: String, code: Option[String] = None) {
  def toOffer(implicit timer: LocalDateTimeProvider): Option[Offer] = {
    val listOfProducts = products.mkString(", ")
    val offerCode = code.getOrElse("")
    Offer.fromStrings(description, listOfProducts, price, validFor, offerCode)
  }
}

object OfferRequestBody {
  implicit val jsonEncoder: Encoder[OfferRequestBody] = deriveEncoder[OfferRequestBody]
  implicit val jsonDecoder: Decoder[OfferRequestBody] = deriveDecoder[OfferRequestBody]
  implicit val entityDecoder: EntityDecoder[IO, OfferRequestBody] = jsonOf[IO, OfferRequestBody]
}