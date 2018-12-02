package com.personal.hourstracker.registration

import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.registration.domain.DBRegistration

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
    new DBRegistration(
      id = registration.id,
      job = registration.job,
      clockedIn = registration.clockedIn,
      clockedOut = registration.clockedOut)

  implicit def toRegistrations(registrations: Iterable[DBRegistration]): Iterable[Registration] =
    registrations.map(toRegistration)

  implicit def toRegistration(registration: DBRegistration): Registration =
    Registration(
      id = Some(registration._id),
      job = registration._job,
      clockedIn = registration._clockedIn,
      clockedOut = registration._clockedOut)
}
