package com.spikerlabs.offers

import java.time.LocalDateTime

import com.spikerlabs.offers.domain.Offer

case class OfferResponseBody(products: List[String], price: String, validUntil: LocalDateTime, description: String, code: String)

object OfferResponseBody {
  def fromOffer(offer: Offer): OfferResponseBody = {
    OfferResponseBody(
      offer.articleIds.map(_.value),
      s"£${offer.discount.value.toShortExact}",
      offer.validFor.value,
      offer.description,
      offer.code.value
    )
  }
}