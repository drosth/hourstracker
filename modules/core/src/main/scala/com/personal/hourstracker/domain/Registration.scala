package com.personal.hourstracker.domain

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.personal.common.CommonJsonSupport
import spray.json._

object Registration {
  type RegistrationID = Long
  type Registrations = List[Registration]

  object JsonProtocol extends CommonJsonSupport {
    implicit lazy val registrationFormat: RootJsonFormat[Registration] =
      jsonFormat14(Registration.apply)
  }

}

final case class Registration(
  id: Option[Registration.RegistrationID] = None,
  job: String,
  clockedIn: Option[LocalDateTime] = None,
  clockedOut: Option[LocalDateTime] = None,
  duration: Option[Double] = None,
  hourlyRate: Option[Double] = None,
  earnings: Option[Double] = None,
  comment: Option[String] = None,
  tags: Option[Set[String]] = None,
  breaks: Option[String] = None,
  adjustments: Option[String] = None,
  totalTimeAdjustment: Option[Double] = None,
  totalEarningsAdjustment: Option[Double] = None,
  totalKilometrage: Option[String] = None) {

  def calculateDurationInMinutes(): Long = (clockedIn, clockedOut) match {
    case (Some(in: LocalDateTime), Some(out: LocalDateTime)) =>
      ChronoUnit.MINUTES.between(in, out)
    case _ => 0L
  }
}
