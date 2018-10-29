package com.spikerlabs.offers

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode}
import com.spikerlabs.offers.storage.OfferStore

import scala.util.Try

case class OffersService(private val offerStore: OfferStore)
                        (implicit timer: LocalDateTimeProvider) {
  def getOffers(product: Offer.Product): List[Offer] = offerStore.getOffers(product)

  def getOffer(code: OfferCode): Option[Offer] = offerStore.getOffer(code)

  def addOffer(offer: Offer): Try[Unit] = Try(offerStore.store(offer)).map(_ => ())

  def cancelOffer(code: OfferCode): Unit = offerStore.cancelOffer(code)
}

object OffersService {
  def withStore(store: OfferStore)(implicit timer: LocalDateTimeProvider): OffersService = new OffersService(store)
}