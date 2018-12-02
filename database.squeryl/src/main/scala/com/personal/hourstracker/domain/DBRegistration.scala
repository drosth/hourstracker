package com.personal.hourstracker.domain

import java.sql.Timestamp
import java.time.LocalDateTime

case class DBRegistration(
  job: String,
  clockedIn: Option[Timestamp] = None,
  clockedOut: Option[Timestamp] = None,
  duration: Option[Double] = None,
  hourlyRate: Option[Double] = None,
  earnings: Option[Double] = None,
  comment: Option[String] = None,
  //                         tags: Option[Set[String]] = None,
  breaks: Option[String] = None,
  adjustments: Option[String] = None,
  totalTimeAdjustment: Option[Double] = None,
  totalEarningsAdjustment: Option[String] = None) extends HourstrackerDBObject
  with java.io.Serializable {

  def this() = this(
    job = "",
    clockedIn = Some(Timestamp.valueOf(LocalDateTime.now())),
    clockedOut = Some(Timestamp.valueOf(LocalDateTime.now())),
    duration = Some(0.0),
    hourlyRate = Some(0.0),
    earnings = Some(0.0),
    comment = Some(""),
    //                         tags: Option[Set[String]] = None,
    breaks = Some(""),
    adjustments = Some(""),
    totalTimeAdjustment = Some(0.0),
    totalEarningsAdjustment = Some(""))
}
