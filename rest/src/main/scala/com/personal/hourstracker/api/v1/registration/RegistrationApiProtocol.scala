package com.personal.hourstracker.api.v1.registration
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.personal.common.CommonJsonSupport
import com.personal.hourstracker.api.v1.registration.RegistrationApi.RegistrationModel
import spray.json.RootJsonFormat

trait RegistrationApiProtocol extends CommonJsonSupport with SprayJsonSupport {
  implicit val registrationFormat: RootJsonFormat[RegistrationModel] =
    jsonFormat12(RegistrationModel.apply)
}