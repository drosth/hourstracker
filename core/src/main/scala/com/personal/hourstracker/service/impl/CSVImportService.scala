package com.personal.hourstracker.service.impl

import java.io.{ File, FileInputStream, InputStreamReader, Reader }

import com.github.tototoshi.csv.CSVReader
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.{ ImportService, _ }

import scala.concurrent.{ ExecutionContext, Future }

object CSVImportService {
  def toReader(fileName: String): Reader = {
    toReader(new File(fileName))
  }

  def toReader(file: File): Reader = {
    new InputStreamReader(new FileInputStream(file), DEFAULT_ENCODING)
  }

  private def readTagsFrom(value: Option[String]): Option[Set[String]] =
    value match {
      case None => None
      case Some(tags) => Some(tags.split(";").toSet)
    }
}

class CSVImportService()(implicit executionContext: ExecutionContext) extends ImportService {

  import CSVImportService._

  override def importRegistrationsFrom(fileName: String): Future[Registrations] =
    Future({
      val csvReader: CSVReader = CSVReader.open(toReader(fileName))

      csvReader.readNext() // skip the "sep" line
      csvReader.toStreamWithHeaders
        .map(
          record =>
            Registration(
              None,
              record.getOrElse("Job", ""),
              record.get("Clocked In"),
              record.get("Clocked Out"),
              record.get("Duration"),
              record.get("Hourly Rate"),
              record.get("Earnings"),
              record.get("Comment"),
              readTagsFrom(record.get("Tags")),
              record.get("TotalTimeAdjustment"),
              record.get("TotalEarningsAdjustment")))
        .toList
    })
}
