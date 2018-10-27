package com.spikerlabs.offers

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.LocalDateTimeProvider
import com.spikerlabs.offers.storage.OfferStore

import scala.util.Try

case class OffersService(private val offerStore: OfferStore)
                        (implicit timer: LocalDateTimeProvider) {
  def getOffers(product: Offer.Product): List[Offer] = offerStore.getOffers(product)

  def addOffer(offer: Offer): Try[Unit] = Try(offerStore.store(offer)).map(_ => ())
}

object OffersService {
  def withStore(store: OfferStore)(implicit timer: LocalDateTimeProvider): OffersService = new OffersService(store)
}