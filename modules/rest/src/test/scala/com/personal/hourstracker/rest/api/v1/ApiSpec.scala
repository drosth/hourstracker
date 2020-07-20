package com.personal.hourstracker.rest.api.v1

import scala.concurrent.ExecutionContext

trait ApiSpec
  extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfter
    with MockitoSugar
    with ScalatestRouteTest
    with SystemComponent
    with LoggingComponent
    with Configuration {

  override def executionContext: ExecutionContext = system.dispatcher
}
