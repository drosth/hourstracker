package com.personal.hourstracker.service

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import com.personal.hourstracker.domain.Registration

trait Selector {
  def filter: Registration => Boolean
}

object RegistrationSelector {
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit def optionalStringToLocalDate(value: Option[String]): Option[LocalDate] = value.map(x => x)

  implicit def stringToLocalDate(value: String): LocalDate = LocalDate.parse(value, dateTimeFormatter)

  final case class RegistrationRangeSelector(startAt: Option[LocalDate], endAt: Option[LocalDate]) extends Selector {
    implicit class LocalDateOps(source: LocalDate) {
      def isGreaterThanOrEqualTo(boundary: LocalDate): Boolean = source.isAfter(boundary) || source.isEqual(boundary)
      def isLessThanOrEqualTo(boundary: LocalDate): Boolean    = source.isBefore(boundary) || source.isEqual(boundary)
    }

    private def isInRange(source: LocalDate): Boolean = (startAt, endAt) match {
      case (None, None)             => true
      case (Some(start), None)      => source.isGreaterThanOrEqualTo(start)
      case (None, Some(end))        => source.isLessThanOrEqualTo(end)
      case (Some(start), Some(end)) => source.isGreaterThanOrEqualTo(start) && source.isLessThanOrEqualTo(end)
    }

    private def isInRange(source: LocalDateTime): Boolean =
      isInRange(source.toLocalDate)

    override lazy val filter: Registration => Boolean = registration => registration.clockedIn.exists(isInRange)
  }

  final case class RegistrationInYearSelector(year: Int = LocalDateTime.now().getYear) extends Selector {
    override lazy val filter: Registration => Boolean = registration => registration.clockedIn.exists(value => value.getYear == year)
  }
}
