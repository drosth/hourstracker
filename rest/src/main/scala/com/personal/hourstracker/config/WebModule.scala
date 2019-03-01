package com.personal.hourstracker.config

import akka.stream.ActorMaterializer

trait WebModule extends ApplicationModule {

  implicit val materializer: ActorMaterializer = ActorMaterializer()
}
