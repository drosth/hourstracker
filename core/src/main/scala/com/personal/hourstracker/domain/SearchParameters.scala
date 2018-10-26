package com.personal.hourstracker.domain

import java.time.{ LocalDate, Month }
import java.time.format.{ DateTimeFormatter, TextStyle }
import java.util.Locale

case class SearchParameters(startAt: Option[LocalDate], endAt: Option[LocalDate])

object SearchParameters {
  final val UndefinedSearchParameters = new SearchParameters(None, None)

  def hasEqualMonth(startAt: Option[LocalDate], endAt: Option[LocalDate]): Boolean = startAt match {
    case None => endAt.isEmpty
    case Some(startingAt) => endAt match {
      case None => false
      case Some(endingAt) => startingAt.withDayOfMonth(1).equals(endingAt.withDayOfMonth(1))
      case _ => false
    }
  }

  def correctLocalDateBasedOnStartAt(endAt: Option[LocalDate], startAt: Option[LocalDate]): Option[LocalDate] = endAt match {
    case None => None
    case Some(endingAt) if hasEqualMonth(startAt, endAt) => Some(endingAt.plusMonths(1).withDayOfMonth(1).minusDays(1))
  }

  def apply(startAt: Option[String], endAt: Option[String] = None)(implicit locale: Locale): SearchParameters = {
    val endingAt: Option[LocalDate] = correctLocalDateBasedOnStartAt(endAt, startAt)
    new SearchParameters(startAt, endingAt)
  }

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def isFullNameOfMonth(value: String)(implicit locale: Locale): Boolean = {
    Month
      .values()
      .map(_.getDisplayName(TextStyle.FULL, locale))
      .find(_.equalsIgnoreCase(value)) match {
        case Some(_) => true
        case None => false
      }
  }

  def isShortNameOfMonth(value: String)(implicit locale: Locale): Boolean = {
    Month
      .values()
      .map(_.getDisplayName(TextStyle.SHORT, locale))
      .find(_.equalsIgnoreCase(value)) match {
        case Some(_) => true
        case None => false
      }
  }

  def nameOfMonthToLocalDate(
    nameOfMonth: String,
    pattern: String,
    year: Int = LocalDate.now().getYear)(implicit locale: Locale): LocalDate =
    LocalDate.parse(s"$year-$nameOfMonth-1", DateTimeFormatter.ofPattern(pattern, locale))

  def weekNumberToLocalDate(weekNumber: Int, year: Int = LocalDate.now().getYear)(implicit locale: Locale): LocalDate =
    LocalDate.parse(s"$year-W$weekNumber-1", DateTimeFormatter.ISO_WEEK_DATE)

  implicit def stringToLocalDate(value: Option[String])(implicit locale: Locale): Option[LocalDate] = value match {
    case None => None
    case Some(stringValue) =>
      stringValue match {
        case r"wk(\d{1,2})${ weekNumber }" => Some(weekNumberToLocalDate(weekNumber.toInt))
        case r"[wW](\d{1,2})${ weekNumber }" => Some(weekNumberToLocalDate(weekNumber.toInt))
        case v if isFullNameOfMonth(v) => Some(nameOfMonthToLocalDate(v, "yyyy-MMMM-d"))
        case v if isShortNameOfMonth(v) => Some(nameOfMonthToLocalDate(v, "yyyy-MMM-d"))
        case _ => Some(LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE))
      }
  }
}
