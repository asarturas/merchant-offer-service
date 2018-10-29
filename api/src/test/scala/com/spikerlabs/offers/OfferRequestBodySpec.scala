package com.spikerlabs.offers

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode, SpecialPrice, ValidUntil}
import org.scalatest.{AppendedClues, FlatSpec, Matchers}

class OfferRequestBodySpec extends FlatSpec with Matchers with AppendedClues {

  it should "return some offer when data is valid" in {
    val offerBody = OfferRequestBody(List("A123"), "£10", "1 day", "some description", Some("CODE12"))
    val date = LocalDateTime.parse("1970-01-01T00:00:00")
    implicit val timer: LocalDateTimeProvider = () => date
    offerBody.toOffer shouldBe
      Some(Offer("some description", List(Offer.Product("A123")), SpecialPrice(10), ValidUntil(LocalDateTime.parse("1970-01-02T00:00:00")), OfferCode("CODE12")))
  }

}
