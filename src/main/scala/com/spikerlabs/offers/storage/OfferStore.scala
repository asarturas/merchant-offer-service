package com.spikerlabs.offers.storage

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.ArticleId
import com.spikerlabs.offers.storage.errors.StoreError

trait OfferStore {
  def store(offer: Offer): Either[StoreError, OfferStore]
  def getOffers(article: ArticleId): List[Offer]
}
