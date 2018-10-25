package steps

import com.spikerlabs.offers.storage.InMemoryOfferStore
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers

trait StatefulSteps[A] extends ScalaDsl with EN with Matchers {
  var state: A
}

object StatefulSteps {
  trait State
  case class OfferServiceState(store: InMemoryOfferStore = new InMemoryOfferStore) extends State
}