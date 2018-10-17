package com.personal.hourstracker.repository

import java.io.Reader

import scala.concurrent.Future

import com.github.tototoshi.csv.CSVReader
import com.personal.hourstracker.config.component.{ RegistrationRepository, SystemComponent }
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations

trait DefaultRegistrationRepository extends RegistrationRepository {
  this: SystemComponent =>

  def registrationRepository: RegistrationRepository =
    new DefaultRegistrationRepository()

  private def readTagsFrom(value: Option[String]): Option[Set[String]] =
    value match {
      case None => None
      case Some(tags) => Some(tags.split(";").toSet)
    }

  class DefaultRegistrationRepository() extends RegistrationRepository {

    override def readRegistrationsFrom(reader: Reader): Future[Registrations] =
      Future({
        val csvReader: CSVReader = CSVReader.open(reader)

        csvReader.readNext() // skip the "sep" line
        csvReader.toStreamWithHeaders
          .map(
            record =>
              Registration(
                record.getOrElse("Job", ""),
                record.get("Clocked In"),
                record.get("Clocked Out"),
                record.get("Duration"),
                record.get("Hourly Rate"),
                record.get("Earnings"),
                record.get("Comment"),
                readTagsFrom(record.get("Tags")),
                record.get("Breaks"),
                record.get("Adjustments"),
                record.get("TotalTimeAdjustment"),
                record.get("TotalEarningsAdjustment")))
          .toList
      })
  }
}
