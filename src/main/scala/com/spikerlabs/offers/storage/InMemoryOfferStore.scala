package com.spikerlabs.offers.storage
import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.ArticleId
import com.spikerlabs.offers.storage.errors.StoreError

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

class InMemoryOfferStore extends OfferStore {
  private val inMemoryStorage = TrieMap[ArticleId, List[Offer]]()
  private def concurrencyError = new StoreError("was unable to store the offer as value is already updated in storage")

  def store(offer: Offer): Either[StoreError, OfferStore] = {
    if (!inMemoryStorage.contains(offer.articleId))
      inMemoryStorage.putIfAbsent(offer.articleId, Nil)
    val previousValue = inMemoryStorage.getOrElse(offer.articleId, Nil)
    val newValue = offer :: previousValue
    if (inMemoryStorage.replace(offer.articleId, previousValue, newValue)) Right(this)
    else Left(concurrencyError)
  }

  def getOffers(article: Offer.ArticleId): List[Offer] =
    inMemoryStorage.getOrElse(article, Nil)
}
