package com.spikerlabs.offers

import cats.effect.IO
import com.spikerlabs.offers.domain.Offer.utcLocalDateTime
import com.spikerlabs.offers.storage.InMemoryOfferStore
import fs2.StreamApp

import scala.concurrent.ExecutionContext.Implicits.global

object App extends StreamApp[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    OffersApi.forService(OffersService.withStore(new InMemoryOfferStore)).httpStream().serve
}
