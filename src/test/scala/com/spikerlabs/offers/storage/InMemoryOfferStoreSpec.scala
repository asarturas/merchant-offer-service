package com.spikerlabs.offers.storage

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.ArticleId
import org.scalatest.{FlatSpec, Matchers}

class InMemoryOfferStoreSpec extends FlatSpec with Matchers {
  it should "return an empty list when there are no matching offers" in {
    val store: OfferStore = new InMemoryOfferStore()
    store.getOffers(ArticleId("A123")) shouldBe empty
  }
  it should "return itself on successful store of an offer" in {
    val store: OfferStore = new InMemoryOfferStore()
    val offer = Offer.fromStrings("desc", "A123", "£10", "1 day").get
    store.store(offer) shouldBe Right(store)
  }
  it should "return single matching offer as a list" in {
    val store: OfferStore = new InMemoryOfferStore()
    val offer = Offer.fromStrings("desc", "A123", "£10", "1 day").get
    store.store(offer)
    store.getOffers(ArticleId("A123")) shouldBe List(offer)
  }
  it should "return all the matching offers" in {
    val store: OfferStore = new InMemoryOfferStore()
    val oneOffer = Offer.fromStrings("desc1", "A123", "£10", "1 day").get
    val otherOffer = Offer.fromStrings("desc2", "A123", "£20", "1 day").get
    store.store(oneOffer).flatMap(_.store(otherOffer)) shouldBe Right(store)
    store.getOffers(ArticleId("A123")) should contain allElementsOf List(oneOffer, otherOffer)
  }
  it should "return matching offers when store contains offers for more than one article" in {
    val store: OfferStore = new InMemoryOfferStore()
    val oneArticle = "A123"
    val oneOffer = Offer.fromStrings("desc1", oneArticle, "£10", "1 day").get
    val otherArticle = "B321"
    val otherOffer = Offer.fromStrings("desc2", otherArticle, "£20", "1 day").get
    store.store(oneOffer).flatMap(_.store(otherOffer)) shouldBe Right(store)
    store.getOffers(ArticleId(otherArticle)) should contain allElementsOf List(otherOffer)
  }
}
