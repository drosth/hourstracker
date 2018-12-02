package com.personal.hourstracker.domain

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.personal.common.CommonJsonSupport
import spray.json._

object Registration {

  type Registrations = List[Registration]

  object JsonProtocol extends CommonJsonSupport {

    implicit lazy val registrationFormat: RootJsonFormat[Registration] =
      jsonFormat13(Registration.apply)
  }

}

case class Registration(
  id: Option[Long] = None,
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
  totalEarningsAdjustment: Option[String] = None) {

  def this() = this(job = "undefined", tags = Some(Set[String]("")))

  def calculateDurationInMinutes(): Long = (clockedIn, clockedOut) match {
    case (Some(in: LocalDateTime), Some(out: LocalDateTime)) =>
      ChronoUnit.MINUTES.between(in, out)
    case _ => 0L
  }
}
