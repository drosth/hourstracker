package com.personal.hourstracker

import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.domain.{ DBRegistration, Registration }

package object repository {

  implicit def toTimestamp(value: LocalDateTime): Timestamp =
    Timestamp.valueOf(value)

  implicit def toOption[A, B](value: Option[A]): Option[B] = value match {
    case None => None
    case Some(v) => Some(v.asInstanceOf[B])
  }

  implicit def toDBRegistrations(registrations: List[Registration]): List[DBRegistration] =
    registrations.map(toDBRegistration)

  implicit def toDBRegistration(registration: Registration): DBRegistration =
    DBRegistration(
      job = registration.job,
      clockedIn = registration.clockedIn,
      clockedOut = registration.clockedOut,
      duration = registration.duration,
      hourlyRate = registration.hourlyRate,
      earnings = registration.earnings,
      comment = registration.comment,
      //  //                         tags: Option[Set[String]] = None,
      breaks = registration.breaks,
      adjustments = registration.adjustments,
      totalTimeAdjustment = registration.totalTimeAdjustment,
      totalEarningsAdjustment = registration.totalEarningsAdjustment)
}
