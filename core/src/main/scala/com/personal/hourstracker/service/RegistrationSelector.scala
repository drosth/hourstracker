package com.personal.hourstracker.service

import java.time.format.DateTimeFormatter
import java.time.{ LocalDate, LocalDateTime }

import com.personal.hourstracker.domain.Registration

trait Selector {
  def filter: Registration => Boolean
}

object RegistrationSelector {
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit def optionalStringToLocalDate(value: Option[String]): Option[LocalDate] = value.map(x => x)

  implicit def stringToLocalDate(value: String): LocalDate = LocalDate.parse(value, dateTimeFormatter)

  final class RegistrationRangeSelector(startAt: Option[LocalDate], endAt: Option[LocalDate]) extends Selector {
    private def isInRange(source: LocalDateTime): Boolean = {
      startAt.exists(lower => source.toLocalDate.isAfter(lower.minusDays(1))) ||
        endAt.exists(upper => source.toLocalDate.isBefore(upper))
    }

    override lazy val filter: Registration => Boolean = registration => registration.clockedIn.exists(isInRange)
  }

  final class RegistrationInYearSelector(year: Int = LocalDateTime.now().getYear) extends Selector {
    override lazy val filter: Registration => Boolean = registration => registration.clockedIn.exists(value => value.getYear == year)
  }
}
