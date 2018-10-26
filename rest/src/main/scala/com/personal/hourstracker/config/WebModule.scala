package com.personal.hourstracker.config
import akka.stream.ActorMaterializer

trait WebModule extends ApplicationModule {

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //  override implicit def locale: Locale = new Locale("nl", "NL")
}
