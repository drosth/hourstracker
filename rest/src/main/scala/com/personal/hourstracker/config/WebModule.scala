package com.personal.hourstracker.config

import akka.stream.ActorMaterializer

trait WebModule extends ApplicationModule with StorageModule {

  implicit val materializer: ActorMaterializer = ActorMaterializer()
}
