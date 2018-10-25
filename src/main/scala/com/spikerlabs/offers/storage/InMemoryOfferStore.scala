package com.spikerlabs.offers.storage
import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{ArticleId, OfferId}
import com.spikerlabs.offers.storage.errors.StoreError

import scala.collection.concurrent.TrieMap

class InMemoryOfferStore extends OfferStore {
  private val inMemoryStorage = TrieMap[OfferId, Offer]()
  private def concurrencyError = new StoreError("was unable to store the offer as value is already updated in storage")

  def store(offer: Offer): Either[StoreError, OfferStore] = {
    inMemoryStorage.putIfAbsent(offer.id, offer) match {
      case None => Right(this)
      case Some(previousValue) =>
        if (inMemoryStorage.replace(offer.id, previousValue, offer)) Right(this)
        else Left(concurrencyError)
    }
  }

  def getOffers(article: Offer.ArticleId): List[Offer] =
    inMemoryStorage.filter {
      case (_, Offer(_, offerArticle, _, _, _)) if article == offerArticle => true
      case _ => false
    }.values.toList
}
