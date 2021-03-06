package steps

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{LocalDateTimeProvider, OfferCode, OfferCodeGenerator}
import cucumber.api.DataTable

import scala.collection.JavaConverters._

trait Transformers {

  def singleOfferTableToOffer(table: DataTable)
                             (implicit timer: LocalDateTimeProvider): Offer =
    singleOfferInformationToOffer(singleOfferTableToInformation(table))

  private def singleOfferTableToInformation(singleOfferTable: DataTable): Map[String, String] =
    singleOfferTable.asMap(classOf[String], classOf[String]).asScala.toMap

  private def singleOfferInformationToOffer(singleOfferInformation: Map[String, String])
                                           (implicit timer: LocalDateTimeProvider): Offer = {
    implicit val generateIdByArticleId: OfferCodeGenerator = () =>
      OfferCode(singleOfferInformation("products"))
    Offer.fromStrings(
      description = singleOfferInformation("description"),
      lostOfProducts = singleOfferInformation("products"),
      discount = singleOfferInformation("price"),
      validForOrUntilDate = singleOfferInformation.getOrElse("valid until", singleOfferInformation("valid for")),
      offerCode = singleOfferInformation.getOrElse("code", "")
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
