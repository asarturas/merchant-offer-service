package com.spikerlabs.offers.storage

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.Product
import com.spikerlabs.offers.storage.errors.StoreError

trait OfferStore {
  def store(offer: Offer): Either[StoreError, OfferStore]
  def getOffers(product: Product): List[Offer]
}
