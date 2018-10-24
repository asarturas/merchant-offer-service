package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers

trait StatefulSteps[A] extends ScalaDsl with EN with Matchers {
  var state: A
}

object StatefulSteps {
  trait State
  case class OfferServiceState() extends State
}