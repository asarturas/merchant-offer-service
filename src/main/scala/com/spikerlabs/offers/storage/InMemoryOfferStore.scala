package com.spikerlabs.offers.storage

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode}
import com.spikerlabs.offers.storage.errors.StoreError

import scala.collection.concurrent.TrieMap

class InMemoryOfferStore extends OfferStore {

  private val inMemoryStorage = TrieMap[OfferCode, Offer]()

  private def concurrencyError = new StoreError("was unable to store the offer as value is already updated in storage")

  def store(offer: Offer): Either[StoreError, OfferStore] = {
    if (inMemoryStorage.contains(offer.code)) updateOffer(offer)
    else insertOffer(offer)
  }

  // fail when trying to update a value, which was already updated (concurrency issue)
  private def updateOffer(offer: Offer): Either[StoreError, OfferStore] =
    inMemoryStorage.get(offer.code)
      .map(previousValue => inMemoryStorage.replace(offer.code, previousValue, offer)) match {
      case Some(true) => Right(this)
      case _ => Left(concurrencyError)
    }

  // fail when trying to insert a value, which is already there (concurrency issue)
  private def insertOffer(offer: Offer): Either[StoreError, OfferStore] =
    inMemoryStorage.putIfAbsent(offer.code, offer) match {
      case None => Right(this)
      case _ => Left(concurrencyError)
    }

  def getOffers(product: Offer.Product)
               (implicit timer: LocalDateTimeProvider): List[Offer] =
    inMemoryStorage.filter {
      case (_, Offer(_, products, _, validUntil, _)) =>
        products.contains(product) && validUntil.value.isAfter(timer())
      case _ => false
    }.values.toList

  def getOffer(id: OfferCode)
              (implicit timer: LocalDateTimeProvider): Option[Offer] =
    inMemoryStorage.get(id).filter(_.validFor.value.isAfter(timer()))
}
