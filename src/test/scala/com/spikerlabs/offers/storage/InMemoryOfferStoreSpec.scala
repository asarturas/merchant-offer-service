package com.spikerlabs.offers.storage

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{Product, SpecialPrice, OfferId, OfferIdGenerator}
import org.scalatest.{FlatSpec, Matchers}

class InMemoryOfferStoreSpec extends FlatSpec with Matchers {

  it should "return an empty list when there are no matching offers" in new Setup {
    store.getOffers(Product("A123")) shouldBe empty
  }

  it should "return itself on successful store of an offer" in new Setup {
    val offer = Offer.fromStrings("desc", "A123", "£10", "1 day", "OFFER1").get
    store.store(offer) shouldBe Right(store)
  }

  it should "return single matching offer as a list" in new Setup {
    val offer = Offer.fromStrings("desc", "A123", "£10", "1 day", "OFFER1").get
    store.store(offer)
    store.getOffers(Product("A123")) shouldBe List(offer)
  }

  it should "return all the matching offers" in new Setup {
    val oneOffer = Offer.fromStrings("desc1", "A123", "£10", "1 day", "OFFER1").get
    val otherOffer = Offer.fromStrings("desc2", "A123", "£20", "1 day", "OFFER2").get
    store.store(oneOffer).flatMap(_.store(otherOffer)) shouldBe Right(store)
    store.getOffers(Product("A123")) should contain allElementsOf List(oneOffer, otherOffer)
  }

  it should "return matching offers when store contains offers for more than one article" in new Setup {
    val oneOffer = Offer.fromStrings("desc1", "A123", "£10", "1 day", "OFFER1").get
    val otherArticle = "B321"
    val otherOffer = Offer.fromStrings("desc2", otherArticle, "£20", "1 day", "OFFER2").get
    store.store(oneOffer).flatMap(_.store(otherOffer)) shouldBe Right(store)
    store.getOffers(Product(otherArticle)) should contain allElementsOf List(otherOffer)
  }

  it should "replace existing value without an issue" in new Setup {
    val offer = Offer.fromStrings("desc", "A123", "£10", "1 day").get
    store.store(offer)
    val updatedOffer = offer.copy(discount = SpecialPrice(15.0))
    store.store(updatedOffer) shouldBe Right(store)
    store.getOffers(Product("A123")) shouldBe List(updatedOffer)
  }

  trait Setup {
    implicit val staticOfferId: OfferIdGenerator = () => OfferId("OFFER1")
    val store: OfferStore = new InMemoryOfferStore()
  }

}
