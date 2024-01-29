package com.personal.hourstracker.api.v1

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{LoggingComponent, SystemComponent}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext

trait ApiSpec
  extends AnyFlatSpec
  with Matchers
  with BeforeAndAfter
  with MockitoSugar
  with ScalatestRouteTest
  with SystemComponent
  with LoggingComponent
  with Configuration {

  override def executionContext: ExecutionContext = system.dispatcher
}
