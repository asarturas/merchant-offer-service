package com.spikerlabs.offers

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

class OffersApi[F[_]](service: OffersService) {
  def service(implicit F: Effect[F]): HttpService[F] = HttpService[F] {
    case GET -> Root / "offer" / _ =>
      F.pure(Response(status = Status.NotFound))
  }
}

object OffersApi {
  def forService[F[_]](service: OffersService): OffersApi[F] =
    new OffersApi[F](service)
}