package com.spikerlabs.offers

import java.time.LocalDateTime

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{OfferCode, SpecialPrice, ValidUntil}
import org.scalatest.{AppendedClues, FlatSpec, Matchers}

class OfferResponseBodySpec extends FlatSpec with Matchers with AppendedClues {

  "offer response factory" should "return a valid offer response from offer domain object priced with pence" in {
    val date = LocalDateTime.parse("1970-01-01T00:00:00")
    val offer = Offer("some description", List(Offer.Product("A123")), SpecialPrice(19.99), ValidUntil(date), OfferCode("OFFER123"))
    OfferResponseBody.fromOffer(offer) shouldBe
      OfferResponseBody(List("A123"), "£19.99", date, "some description", "OFFER123")
  }

  it should "return a valid offer response from offer domain object priced with pounds only" in {
    val date = LocalDateTime.parse("1970-01-01T00:00:00")
    val offer = Offer("some description", List(Offer.Product("A123")), SpecialPrice(20), ValidUntil(date), OfferCode("OFFER123"))
    OfferResponseBody.fromOffer(offer) shouldBe
      OfferResponseBody(List("A123"), "£20", date, "some description", "OFFER123")
  }

}
