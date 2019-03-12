package com.personal.hourstracker.api.v1.registration

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.personal.common.{ CommonJsonSupport, JsonDateTimeSupport }
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import spray.json.RootJsonFormat

trait RegistrationApiProtocol extends CommonJsonSupport with SprayJsonSupport with JsonDateTimeSupport {
  implicit val registrationFormat: RootJsonFormat[RegistrationModel] =
    jsonFormat10(RegistrationModel.apply)
}
