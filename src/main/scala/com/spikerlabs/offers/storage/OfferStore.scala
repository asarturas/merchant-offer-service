package com.spikerlabs.offers.storage

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferId, Product}
import com.spikerlabs.offers.storage.errors.StoreError

trait OfferStore {
  def store(offer: Offer): Either[StoreError, OfferStore]

  def getOffers(product: Product)(implicit timer: LocalDateTimeProvider): List[Offer]

  def getOffer(id: OfferId)(implicit timer: LocalDateTimeProvider): Option[Offer]
}
