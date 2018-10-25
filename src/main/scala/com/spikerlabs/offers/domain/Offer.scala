package com.spikerlabs.offers.domain

import java.util.UUID

import com.spikerlabs.offers.domain.Offer.{ArticleId, Discount, OfferId, ValidFor}

import scala.util.matching.Regex

case class Offer(description: String, articleIds: List[ArticleId], discount: Discount, validFor: ValidFor, id: OfferId)

object Offer {
  type OfferIdGenerator = () => OfferId
  implicit val uniqueOfferIdGenerator: OfferIdGenerator = () => OfferId(UUID.randomUUID().toString)
  def fromStrings(description: String, articleIds: String, discount: String, validFor: String, offerId: String = "")
                 (implicit generateId: OfferIdGenerator): Option[Offer] = {
    val id = if (offerId.nonEmpty) OfferId(offerId) else generateId()
    val articles = articleIds.split(", ").map(ArticleId).toList
    for {
      discount <- Discount.fromString(discount)
      validFor <- ValidFor.fromString(validFor)
    } yield Offer(description, articles, discount, validFor, id)
  }

  case class OfferId(value: String)
  case class ArticleId(value: String)
  case class Discount(value: BigDecimal)
  object Discount {
    def fromString(value: String): Option[Discount] = {
      val regex = new Regex(".(\\d+(\\.\\d+)?)")
      value match {
        case regex(v, _) => Some(Discount(BigDecimal(v)))
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