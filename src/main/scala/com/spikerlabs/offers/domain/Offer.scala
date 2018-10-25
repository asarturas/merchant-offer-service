package com.spikerlabs.offers.domain

import com.spikerlabs.offers.domain.Offer.{ArticleId, Discount, ValidFor}

import scala.util.matching.Regex

case class Offer(description: String, articleId: ArticleId, discount: Discount, validFor: ValidFor)

object Offer {
  def fromStrings(description: String, articleId: String, discount: String, validFor: String): Option[Offer] =
    for {
      discount <- Discount.fromString(discount)
      validFor <- ValidFor.fromString(validFor)
    } yield Offer(description, ArticleId(articleId), discount, validFor)


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