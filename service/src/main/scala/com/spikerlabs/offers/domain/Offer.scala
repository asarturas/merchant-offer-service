package com.spikerlabs.offers.domain

import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneOffset}
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.util.UUID

import com.spikerlabs.offers.domain.Offer.{OfferCode, Product, SpecialPrice, ValidUntil}

import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

case class Offer(description: String, articleIds: List[Product], discount: SpecialPrice, validFor: ValidUntil, code: OfferCode)

object Offer {

  type OfferCodeGenerator = () => OfferCode
  type LocalDateTimeProvider = () => LocalDateTime

  implicit val uniqueOfferCodeGenerator: OfferCodeGenerator = () => OfferCode(UUID.randomUUID().toString)
  implicit val utcLocalDateTime: LocalDateTimeProvider = () => LocalDateTime.now(ZoneOffset.UTC)

  def fromStrings(description: String, lostOfProducts: String, discount: String, validForOrUntilDate: String, offerCode: String = "")
                 (implicit generateId: OfferCodeGenerator, timer: LocalDateTimeProvider): Option[Offer] = {
    val code = if (offerCode.nonEmpty) OfferCode(offerCode) else generateId()
    val products = lostOfProducts.split(", ").map(Product).toList
    for {
      specialPrice <- SpecialPrice.fromString(discount)
      validUntil <- ValidUntil.fromString(validForOrUntilDate, timer())
    } yield Offer(description, products, specialPrice, validUntil, code)
  }

  case class OfferCode(value: String)

  case class Product(value: String)

  case class SpecialPrice(value: BigDecimal)

  object SpecialPrice {
    def fromString(value: String): Option[SpecialPrice] = {
      val regex = new Regex(".(\\d+(\\.\\d+)?)")
      value match {
        case regex(v, _) => Some(SpecialPrice(BigDecimal(v)))
        case _ => None
      }
    }
  }

  case class ValidUntil(value: LocalDateTime)

  object ValidUntil {
    def fromString(value: String, baseDateTime: LocalDateTime): Option[ValidUntil] = {
      Try { // attempt to parse date time
        LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      } recover { // attempt to parse date instead
        case _: DateTimeParseException =>
          LocalDateTime.of(LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN)
      } recover { // attempt to parse range instead
        case _: DateTimeParseException =>
          val regex = new Regex("(\\d+) days?")
          value match {
            case regex(v) => baseDateTime.plusDays(v.toLong)
            case _ => throw new Exception("Was not able to parse a date or a range")
          }
      } match {
        case Success(result) => Some(ValidUntil(result))
        case Failure(_) => None
      }
    }
  }

}