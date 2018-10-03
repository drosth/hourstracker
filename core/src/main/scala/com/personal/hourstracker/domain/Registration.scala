package com.personal.hourstracker.domain

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.personal.hourstracker.marshalling.{
  JsonDateTimeSupport,
  JsonMapSupport,
  JsonSupport
}
import spray.json._

object Registration {

  type Registrations = Seq[Registration]

  object JsonProtocol
      extends JsonSupport
      with JsonDateTimeSupport
      with JsonMapSupport {
    implicit lazy val registrationFormat: RootJsonFormat[Registration] =
      jsonFormat12(Registration.apply)
  }
}

case class Registration(job: String,
                        clockedIn: Option[LocalDateTime],
                        clockedOut: Option[LocalDateTime],
                        duration: Option[Double],
                        hourlyRate: Option[Double],
                        earnings: Option[Double],
                        comment: Option[String],
                        tags: Option[Set[String]],
                        breaks: Option[String],
                        adjustments: Option[String],
                        totalTimeAdjustment: Option[Double],
                        totalEarningsAdjustment: Option[String]) {

  def calculateDurationInMinutes(): Long = (clockedIn, clockedOut) match {
    case (Some(in: LocalDateTime), Some(out: LocalDateTime)) =>
      ChronoUnit.MINUTES.between(in, out)
    case _ => 0L
  }
}
