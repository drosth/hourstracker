package com.personal.hourstracker.rest.api.v1.model

import java.time.LocalDateTime

@Schema
final case class RegistrationModel(
                                    id: Option[Registration.RegistrationID] = None,
                                    job: String,
                                    clockedIn: Option[LocalDateTime],
                                    clockedOut: Option[LocalDateTime],
                                    duration: Option[Double],
                                    hourlyRate: Option[Double],
                                    earnings: Option[Double],
                                    comment: Option[String],
                                    tags: Option[Set[String]],
                                    totalTimeAdjustment: Option[Double],
                                    totalEarningsAdjustment: Option[Double]
                                  )

object RegistrationModel {

  trait JsonProtocol extends CommonJsonSupport with SprayJsonSupport with JsonDateTimeSupport {
    implicit lazy val registrationModelFormat: RootJsonFormat[RegistrationModel] = new RootJsonFormat[RegistrationModel] {
      override def write(obj: RegistrationModel): JsValue = JsObject(
        "id" -> obj.id.map(value => JsNumber(value)).getOrElse(JsNull),
        "job" -> JsString(obj.job),
        "clockedIn" -> obj.clockedIn.map(value => jsonDateTime().write(value)).getOrElse(JsNull),
        "clockedOut" -> obj.clockedOut.map(value => jsonDateTime().write(value)).getOrElse(JsNull),
        "duration" -> obj.duration.map(value => JsNumber(value)).getOrElse(JsNull),
        "hourlyRate" -> obj.hourlyRate.map(value => JsNumber(value)).getOrElse(JsNull),
        "earnings" -> obj.earnings.map(value => JsNumber(value)).getOrElse(JsNull),
        "comment" -> obj.comment.map(value => JsString(value)).getOrElse(JsNull),
        "tags" -> obj.tags.map(values => JsArray(values.map(v => JsString(v)).toVector)).getOrElse(JsNull),
        "totalTimeAdjustment" -> obj.totalTimeAdjustment.map(value => JsNumber(value)).getOrElse(JsNull),
        "totalEarningsAdjustment" -> obj.totalEarningsAdjustment.map(value => JsNumber(value)).getOrElse(JsNull)
      )

      override def read(json: JsValue): RegistrationModel = {
        val fields = json.asJsObject.fields
        RegistrationModel(
          id = fields("id").convertTo[Option[Long]],
          job = fields("job").convertTo[String],
          clockedIn = None,
          clockedOut = None,
          duration = fields("duration").convertTo[Option[Double]],
          hourlyRate = fields("hourlyRate").convertTo[Option[Double]],
          earnings = fields("earnings").convertTo[Option[Double]],
          comment = fields("comment").convertTo[Option[String]],
          tags = fields("tags").convertTo[Option[Set[String]]],
          totalTimeAdjustment = fields("totalTimeAdjustment").convertTo[Option[Double]],
          totalEarningsAdjustment = fields("totalEarningsAdjustment").convertTo[Option[Double]]
        )
      }
    }
  }

}
