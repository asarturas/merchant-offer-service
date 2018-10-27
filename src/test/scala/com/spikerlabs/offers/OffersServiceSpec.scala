package com.spikerlabs.offers

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{utcLocalDateTime, OfferCode, Product}
import com.spikerlabs.offers.storage.InMemoryOfferStore
import org.scalatest.{FlatSpec, Matchers}


class OffersServiceSpec extends FlatSpec with Matchers {
  it should "return offers from offer store" in new Setup {
    val offer = Offer.fromStrings("description", "A123", "£100", "1 day").get
    store.store(offer)
    service.getOffers(Product("A123")) shouldBe List(offer)
  }

  it should "store new offer to offer store" in new Setup {
    val offer = Offer.fromStrings("description", "A123", "£100", "1 day").get
    service.addOffer(offer)
    store.getOffers(Product("A123")) shouldBe List(offer)
  }

  it should "get offers by id from offer store" in new Setup {
    val offer = Offer.fromStrings("description", "A123", "£100", "1 day", "OFF123").get
    service.addOffer(offer)
    store.getOffer(OfferCode("OFF123")) shouldBe Some(offer)
  }

  trait Setup {
    val store = new InMemoryOfferStore()
    val service = OffersService.withStore(store)
  }

}
