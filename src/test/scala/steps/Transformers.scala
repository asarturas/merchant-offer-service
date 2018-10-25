package steps

import com.spikerlabs.offers.domain.Offer
import com.spikerlabs.offers.domain.Offer.{OfferId, OfferIdGenerator}
import cucumber.api.DataTable

import collection.JavaConverters._

trait Transformers {
  def singleOfferTableToOffer: DataTable => Offer =
    singleOfferTableToInformation _ andThen singleOfferInformationToOffer
  private def singleOfferTableToInformation(singleOfferTable: DataTable): Map[String, String] =
    singleOfferTable.asMap(classOf[String], classOf[String]).asScala.toMap
  private def singleOfferInformationToOffer(singleOfferInformation: Map[String, String]): Offer = {
    implicit val generateIdByArticleId: OfferIdGenerator = () =>
      OfferId(singleOfferInformation("products"))
    Offer.fromStrings(
      description = singleOfferInformation("description"),
      articleIds = singleOfferInformation("products"),
      discount = singleOfferInformation("price"),
      validFor = singleOfferInformation("valid for")
    ).get
  }

  def multipleOffersTableToOffers: DataTable => List[Offer] =
    multipleOffersTableToInformation _ andThen multipleOffersInformationToOffers
  private def multipleOffersTableToInformation(multipleOfferTable: DataTable): List[Map[String, String]] = {
    val titles = multipleOfferTable.topCells().asScala.toList
    multipleOfferTable.cells(1).asScala
      .map { values =>
        titles.zip(values.asScala).toMap
      }
      .toList
  }
  private def multipleOffersInformationToOffers(multipleOffersInformation: List[Map[String, String]]): List[Offer] = {
    multipleOffersInformation.map(singleOfferInformationToOffer)
  }

}
