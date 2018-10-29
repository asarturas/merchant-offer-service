package com.spikerlabs.offers

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import com.spikerlabs.offers.OffersApi.{OfferBody, OfferResponse}
import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode, SpecialPrice, ValidUntil}
import org.scalatest.{AppendedClues, FlatSpec, Matchers}

class OffersApiSpec extends FlatSpec with Matchers with AppendedClues {

  "offer body" should "return some offer when data is valid" in {
    val offerBody = OfferBody(List("A123"), "£10", "1 day", "some description", Some("CODE12"))
    implicit val timer: LocalDateTimeProvider = () => LocalDateTime.of(LocalDate.parse("1970-01-01", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN)
    offerBody.toOffer shouldBe
      Some(Offer("some description", List(Offer.Product("A123")), SpecialPrice(10), ValidUntil(LocalDateTime.parse("1970-01-02T00:00:00")), OfferCode("CODE12")))
  }

  "offer response factory" should "return a valid offer response from offer domain object" in {
    val date = LocalDateTime.parse("1970-01-01T00:00:00")
    val offer = Offer("some description", List(Offer.Product("A123")), SpecialPrice(20), ValidUntil(date), OfferCode("OFFER123"))
    OfferResponse.fromOffer(offer) shouldBe
      OfferResponse(List("A123"), "£20", date, "some description", "OFFER123")
  }

}
