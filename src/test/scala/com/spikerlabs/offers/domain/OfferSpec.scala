package com.spikerlabs.offers.domain

import com.spikerlabs.offers.domain.Offer.{ArticleId, Discount, ValidFor}
import org.scalatest.{FlatSpec, Matchers}

class OfferSpec extends FlatSpec with Matchers {
  "offer discount factory" should "return some discount for price in pounds with pence" in {
    Discount.fromString("£10.00") shouldBe Some(Discount(10.0))
  }
  it should "return some discount for price in pounds without pence" in {
    Discount.fromString("£10") shouldBe Some(Discount(10.0))
  }
  it should "return none other value" in {
    Discount.fromString("2") shouldBe None
  }
  "valid for factory" should "return some valid for value for any non empty string" in {
    ValidFor.fromString("1 day") shouldBe Some(ValidFor("1 day"))
  }
  it should "return none for an empty string" in {
    ValidFor.fromString("") shouldBe None
  }
  "offer factory" should "return some offer for string with valid data" in {
    Offer.fromStrings("description", "A123", "£10.00", "1 day") shouldBe
      Some(Offer("description", ArticleId("A123"), Discount(10.00), ValidFor("1 day")))
  }
  it should "return none when discount cannot be parsed" in {
    Offer.fromStrings("description", "A123", "x", "1 day") shouldBe
      None
  }
  it should "return none when valid for cannot be parsed" in {
    Offer.fromStrings("description", "A123", "£10", "") shouldBe
      None
  }
}
