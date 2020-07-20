package com.personal.hourstracker.service.presenter

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object PresenterHelper {

  val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d MMM", new Locale("nl", "NL"))

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  def toHumanReadable(value: LocalDate): String = {
    value.format(formatter)
  }

  def toHumanReadableHours(value: Double): String = {
    val hours = value.intValue()
    val min = ((value - hours) * 60).intValue()
    f"$hours:$min%02.0f"
  }

  def toHumanReadableHours(value: Option[Double]): String = value match {
    case None => ""
    case Some(v) => toHumanReadableHours(v)
  }

  val calculateTotalDuration: ConsolidatedRegistrations => Double = registrations =>
    registrations
      .filter(_.duration.isDefined)
      .foldLeft(0d)((a, i) => a + i.duration.get)

  def sortConsolidatedRegistrations(consolidatedRegistrations: ConsolidatedRegistrations): ConsolidatedRegistrations =
    consolidatedRegistrations.sortBy(_.date)
}
