package com.personal.hourstracker.api.v1.consolidatedregistration

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.personal.common.{ CommonJsonSupport, JsonDateTimeSupport }
import com.personal.hourstracker.api.v1.consolidatedregistration.ConsolidatedRegistrationApi.ConsolidatedRegistrationModel
import spray.json.{ DefaultJsonProtocol, RootJsonFormat }

trait ConsolidatedRegistrationApiProtocol
  extends CommonJsonSupport
  with SprayJsonSupport
  with JsonDateTimeSupport
  with DefaultJsonProtocol {

  implicit val consoidatedRegistrationFormat: RootJsonFormat[ConsolidatedRegistrationModel] =
    jsonFormat4(ConsolidatedRegistrationModel.apply)
}
