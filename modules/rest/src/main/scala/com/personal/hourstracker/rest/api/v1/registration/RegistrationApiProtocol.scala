package com.personal.hourstracker.rest.api.v1.registration

trait RegistrationApiProtocol extends CommonJsonSupport with SprayJsonSupport with JsonDateTimeSupport {
  implicit val registrationFormat: RootJsonFormat[RegistrationModel] = jsonFormat11(RegistrationModel.apply)
}
