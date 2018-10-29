package com.spikerlabs.offers

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.LocalDateTimeProvider

case class OfferRequestBody(products: List[String], price: String, validFor: String, description: String, code: Option[String] = None) {
  def toOffer(implicit timer: LocalDateTimeProvider): Option[Offer] = {
    val listOfProducts = products.mkString(", ")
    val offerCode = code.getOrElse("")
    Offer.fromStrings(description, listOfProducts, price, validFor, offerCode)
  }
}