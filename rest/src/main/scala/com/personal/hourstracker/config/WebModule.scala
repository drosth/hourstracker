package com.personal.hourstracker.config
import akka.stream.ActorMaterializer
import com.personal.hourstracker.api.v1.consolidatedregistration.ConsolidatedRegistrationActor
import com.personal.hourstracker.api.v1.registration.RegistrationActor

trait WebModule extends ApplicationModule with RegistrationActor with ConsolidatedRegistrationActor {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
