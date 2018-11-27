package com.personal.hourstracker.service.impl

import java.io.Reader

import scala.concurrent.{ExecutionContext, Future}

import com.github.tototoshi.csv.CSVReader
import com.personal.hourstracker.domain.{Registration, SearchParameters}
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.{ImportService, _}

object CSVImportService {
  private def readTagsFrom(value: Option[String]): Option[Set[String]] =
    value match {
      case None       => None
      case Some(tags) => Some(tags.split(";").toSet)
    }
}

class CSVImportService() extends ImportService {
  import CSVImportService._

  override def importRegistrationsFrom(
    reader: Reader
  )(implicit searchParameters: SearchParameters, executionContext: ExecutionContext): Future[Registrations] =
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
              record.get("TotalEarningsAdjustment")
          )
        )
        .toList
    })
}
