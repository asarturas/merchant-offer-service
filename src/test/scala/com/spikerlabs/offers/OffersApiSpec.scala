package com.spikerlabs.offers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.spikerlabs.offers.domain.Offer.LocalDateTimeProvider
import com.spikerlabs.offers.storage.InMemoryOfferStore
import cats.effect._
import cats.Id
import org.http4s._
import org.http4s.dsl.io._
import org.scalatest.{AppendedClues, FlatSpec, Matchers}

class OffersApiSpec extends FlatSpec with Matchers with AppendedClues {
  it should "return 404 for not existing offer" in new Setup {
    val response: Response[IO] = api.service.orNotFound.run(
      Request(method = Method.GET, uri = Uri.uri("/offer/OFFER404"))
    ).unsafeRunSync
    response.status shouldBe Status.NotFound
    response.body.compile.toVector.unsafeRunSync should be(empty)
  }

  trait Setup {
    implicit val staticTimer: LocalDateTimeProvider = () => LocalDateTime.parse("2010-01-01T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val api = OffersApi.forService[IO](OffersService.withStore(new InMemoryOfferStore))
  }

}
