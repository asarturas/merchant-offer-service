package com.spikerlabs.offers.domain

import com.spikerlabs.offers.domain.Offer.{Product, SpecialPrice, OfferId, OfferIdGenerator, ValidFor}
import org.scalatest.{WordSpec, Matchers}

class OfferSpec extends WordSpec with Matchers {

  "offer discount factory" should {

    "return some discount for price in pounds with pence" in {
      SpecialPrice.fromString("£10.00") shouldBe Some(SpecialPrice(10.0))
    }

    "return some discount for price in pounds without pence" in {
      SpecialPrice.fromString("£10") shouldBe Some(SpecialPrice(10.0))
    }

    "return none other value" in {
      SpecialPrice.fromString("2") shouldBe None
    }

  }

  "valid for factory" should {

    "return some valid for value for any non empty string" in {
      ValidFor.fromString("1 day") shouldBe Some(ValidFor("1 day"))
    }

    "return none for an empty string" in {
      ValidFor.fromString("") shouldBe None
    }

  }

  "offer factory" should {

    "return some offer for string with valid data" in {
      Offer.fromStrings("description", "A123", "£10.00", "1 day", "OFFER2") shouldBe
        Some(Offer("description", List(Product("A123")), SpecialPrice(10.00), ValidFor("1 day"), OfferId("OFFER2")))
    }

    "use offer id generator when no id is passed to the factory" in {
      implicit val staticOfferId: OfferIdGenerator = () => OfferId("OFFER1")
      Offer.fromStrings("description", "A123", "£10", "1 day").get.id shouldBe OfferId("OFFER1")
    }

    "return none when discount cannot be parsed" in {
      Offer.fromStrings("description", "A123", "x", "1 day") shouldBe
        None
    }

    "return none when valid for cannot be parsed" in {
      Offer.fromStrings("description", "A123", "£10", "") shouldBe
        None
    }

  }

}
