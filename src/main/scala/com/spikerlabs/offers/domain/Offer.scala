package com.spikerlabs.offers.domain

import java.util.UUID

import com.spikerlabs.offers.domain.Offer.{Product, SpecialPrice, OfferId, ValidFor}

import scala.util.matching.Regex

case class Offer(description: String, articleIds: List[Product], discount: SpecialPrice, validFor: ValidFor, id: OfferId)

object Offer {

  type OfferIdGenerator = () => OfferId

  implicit val uniqueOfferIdGenerator: OfferIdGenerator = () => OfferId(UUID.randomUUID().toString)

  def fromStrings(description: String, lostOfProducts: String, discount: String, validFor: String, offerId: String = "")
                 (implicit generateId: OfferIdGenerator): Option[Offer] = {
    val id = if (offerId.nonEmpty) OfferId(offerId) else generateId()
    val products = lostOfProducts.split(", ").map(Product).toList
    for {
      specialPrice <- SpecialPrice.fromString(discount)
      validFor <- ValidFor.fromString(validFor)
    } yield Offer(description, products, specialPrice, validFor, id)
  }

  case class OfferId(value: String)

  case class Product(value: String)

  case class SpecialPrice(value: BigDecimal)

  object SpecialPrice {
    def fromString(value: String): Option[SpecialPrice] = {
      val regex = new Regex(".(\\d+(\\.\\d+)?)")
      value match {
        case regex(v, _) => Some(SpecialPrice(BigDecimal(v)))
        case _ => None
      }
    }
  }

  case class ValidFor(value: String)

  object ValidFor {
    def fromString(value: String): Option[ValidFor] =
      if (value.nonEmpty) Some(ValidFor(value))
      else None
  }

}