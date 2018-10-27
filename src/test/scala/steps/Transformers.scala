package steps

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferId, OfferIdGenerator}
import cucumber.api.DataTable

import collection.JavaConverters._

trait Transformers {

  def singleOfferTableToOffer(table: DataTable)
                             (implicit timer: LocalDateTimeProvider): Offer =
    singleOfferInformationToOffer(singleOfferTableToInformation(table))

  private def singleOfferTableToInformation(singleOfferTable: DataTable): Map[String, String] =
    singleOfferTable.asMap(classOf[String], classOf[String]).asScala.toMap

  private def singleOfferInformationToOffer(singleOfferInformation: Map[String, String])
                                           (implicit timer: LocalDateTimeProvider): Offer = {
    implicit val generateIdByArticleId: OfferIdGenerator = () =>
      OfferId(singleOfferInformation("products"))
    Offer.fromStrings(
      description = singleOfferInformation("description"),
      lostOfProducts = singleOfferInformation("products"),
      discount = singleOfferInformation("price"),
      validFor = singleOfferInformation.getOrElse("valid until", singleOfferInformation("valid for"))
    ).get
  }

  def multipleOffersTableToOffers(table: DataTable)
                                 (implicit timer: LocalDateTimeProvider): List[Offer] =
    multipleOffersInformationToOffers(multipleOffersTableToInformation(table))

  private def multipleOffersTableToInformation(multipleOfferTable: DataTable): List[Map[String, String]] = {
    val titles = multipleOfferTable.topCells().asScala.toList
    multipleOfferTable.cells(1).asScala
      .map { values =>
        titles.zip(values.asScala).toMap
      }
      .toList
  }

  private def multipleOffersInformationToOffers(multipleOffersInformation: List[Map[String, String]])
                                               (implicit timer: LocalDateTimeProvider): List[Offer] = {
    multipleOffersInformation.map(singleOfferInformationToOffer)
  }

}
