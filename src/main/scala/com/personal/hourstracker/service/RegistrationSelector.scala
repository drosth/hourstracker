package com.personal.hourstracker.service

import java.time.LocalDateTime
import java.time.temporal.WeekFields

import com.personal.hourstracker.domain.Registration


object RegistrationSelector {

  val registrationsForCurrentYear: Registration => Boolean = registration =>
    registration.clockedIn.isDefined &&
        registration.clockedIn.get.isAfter(startOfYear)

  val registrationsForCurrentMonth: Registration => Boolean = registration =>
    registration.clockedIn.isDefined &&
        registration.clockedIn.get.isAfter(startOfMonth)

  val registrationsForCurrentWeek: Registration => Boolean = registration =>
    registrationsForCurrentYear(registration) &&
        currentWeeknumber == extractWeeknumberFrom(registration.clockedIn)

  private def startOfYear = LocalDateTime.now().withMonth(1).withDayOfMonth(1)

  private def startOfMonth = LocalDateTime.now().withDayOfMonth(1)

  private def currentWeeknumber: Int = extractWeeknumberFrom(Some(LocalDateTime.now()))

  private def extractWeeknumberFrom(dateTime: Option[LocalDateTime]): Int = dateTime map {
    _.get(WeekFields.ISO.weekOfYear())
  } getOrElse 0

  private def extractYearFrom(dateTime: Option[LocalDateTime]): Int = dateTime map {
    _.getYear
  } getOrElse 0
}
