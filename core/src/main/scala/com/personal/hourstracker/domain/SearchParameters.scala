package com.personal.hourstracker.domain

import java.time.{ LocalDate, Month }
import java.time.format.{ DateTimeFormatter, TextStyle }
import java.util.Locale

case class SearchParameters(startAt: Option[LocalDate], endAt: Option[LocalDate])

object SearchParameters {
  final val UndefinedSearchParameters = new SearchParameters(None, None)

  def apply(startAt: Option[String], endAt: Option[String])(implicit locale: Locale): SearchParameters = {
    new SearchParameters(startAt, endAt)
  }

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def isNameOfMonth(value: String)(implicit locale: Locale): Boolean = {
    Month
      .values()
      .map(_.getDisplayName(TextStyle.FULL, locale))
      .find(_.equalsIgnoreCase(value)) match {
        case Some(_) => true
        case None => false
      }
  }

  def nameOfMonthToLocalDate(nameOfMonth: String, year: Int = LocalDate.now().getYear)(implicit locale: Locale): LocalDate =
    LocalDate.parse(s"$year-$nameOfMonth-1", DateTimeFormatter.ofPattern("yyyy-MMMM-d", locale))

  def weekNumberToLocalDate(weekNumber: Int, year: Int = LocalDate.now().getYear)(implicit locale: Locale): LocalDate =
    LocalDate.parse(s"$year-W$weekNumber-1", DateTimeFormatter.ISO_WEEK_DATE)

  implicit def stringToLocalDate(value: Option[String])(implicit locale: Locale): Option[LocalDate] = value match {
    case None => None
    case Some(stringValue) =>
      stringValue match {
        case r"wk(\d{1,2})${ weekNumber }" => Some(weekNumberToLocalDate(weekNumber.toInt))
        case r"[wW](\d{1,2})${ weekNumber }" => Some(weekNumberToLocalDate(weekNumber.toInt))
        case v if isNameOfMonth(v) => Some(nameOfMonthToLocalDate(v))
        case _ => Some(LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE))
      }
  }
}
