package com.spikerlabs.offers.domain

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode, OfferCodeGenerator, Product, SpecialPrice, ValidUntil}
import org.scalatest.{Matchers, WordSpec}

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

  "valid until factory" should {

    val baseTime = LocalDateTime.parse("1970-01-01T00:00:00")

    "return valid until date for 1 day" in {
      ValidUntil.fromString("1 day", baseTime) match {
        case Some(ValidUntil(date)) => date.isEqual(baseTime.plusDays(1))
        case otherResult => fail(s"unexpected result $otherResult")
      }
    }

    "return valid until date for 2 days" in {
      ValidUntil.fromString("2 days", baseTime) match {
        case Some(ValidUntil(date)) => date.isEqual(baseTime.plusDays(2))
        case otherResult => fail(s"unexpected result $otherResult")
      }
    }

    "return valid until date when given a date" in {
      ValidUntil.fromString("2010-01-01", baseTime) match {
        case Some(ValidUntil(date)) => date.isEqual(LocalDateTime.parse("2010-01-01T00:00:00"))
        case otherResult => fail(s"unexpected result $otherResult")
      }
    }

    "return valid until date when given a date time" in {
      ValidUntil.fromString("2010-01-01T01:02:03", baseTime) match {
        case Some(ValidUntil(date)) => date.isEqual(LocalDateTime.parse("2010-01-01T01:02:03"))
        case otherResult => fail(s"unexpected result $otherResult")
      }
    }

    "return none for an empty string" in {
      ValidUntil.fromString("", baseTime) shouldBe None
    }

    "return none for a string, which cannot be parsed" in {
      ValidUntil.fromString("asdf", baseTime) shouldBe None
    }

  }

  "offer factory by strings" should {

    "return some offer for string with valid data" in {
      implicit val timer: LocalDateTimeProvider = () => LocalDateTime.of(LocalDate.parse("1970-01-01", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN)
      Offer.fromStrings("description", "A123", "£10.00", "1 day", "OFFER2") shouldBe
        Some(Offer("description", List(Product("A123")), SpecialPrice(10.00), ValidUntil(LocalDateTime.of(LocalDate.parse("1970-01-02", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN)), OfferCode("OFFER2")))
    }

    "use offer code generator when no code is passed to the factory" in {
      implicit val staticOfferCode: OfferCodeGenerator = () => OfferCode("OFFER1")
      import Offer.utcLocalDateTime
      Offer.fromStrings("description", "A123", "£10", "1 day").get.code shouldBe OfferCode("OFFER1")
    }

    "return none when discount cannot be parsed" in {
      import Offer.utcLocalDateTime
      Offer.fromStrings("description", "A123", "x", "1 day") shouldBe
        None
    }

    "return none when valid for cannot be parsed" in {
      import Offer.utcLocalDateTime
      Offer.fromStrings("description", "A123", "£10", "") shouldBe
        None
    }

  }

}
