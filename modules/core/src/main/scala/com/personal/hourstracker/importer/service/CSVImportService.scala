package com.personal.hourstracker.importer.service

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.github.tototoshi.csv.CSVReader
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.{ImporterService, _}

import java.io.{File, FileInputStream, InputStreamReader, Reader}
import scala.concurrent.{ExecutionContext, Future}

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

class CSVImportService()(implicit executionContext: ExecutionContext) extends ImporterService {

  import CSVImportService._

  override def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]] =
    Future.successful(try {
      val csvReader: CSVReader = CSVReader.open(toReader(fileName))

      csvReader.readNext() // skip the "sep" line

      val registrations: Registrations = csvReader.toStreamWithHeaders
        .map(
          record =>
            Registration(
              id = None,
              job = record.getOrElse("Job", ""),
              clockedIn = record.get("Clocked In"),
              clockedOut = record.get("Clocked Out"),
              duration = record.get("Duration"),
              hourlyRate = record.get("Hourly Rate"),
              earnings = record.get("Earnings"),
              comment = record.get("Comment"),
              tags = readTagsFrom(record.get("Tags")),
              breaks = record.get("Breaks"),
              adjustments = record.get("Adjustments"),
              totalTimeAdjustment = record.get("TotalTimeAdjustment"),
              totalEarningsAdjustment = record.get("TotalEarningsAdjustment"),
              totalKilometrage = record.get("TotalKilometrage")
            ))
        .toList

      Right(registrations)

    } catch {
      case e: Throwable => Left(e.getMessage)
    })

  def importRegistrationsFromSource(fileName: String): Either[String, Source[Registration, NotUsed]] =
    try {
      val csvReader: CSVReader = CSVReader.open(toReader(fileName))

      csvReader.readNext() // skip the "sep" line

      val registrations = csvReader.toStreamWithHeaders
        .map(
          record =>
            Registration(
              id = None,
              job = record.getOrElse("Job", ""),
              clockedIn = record.get("Clocked In"),
              clockedOut = record.get("Clocked Out"),
              duration = record.get("Duration"),
              hourlyRate = record.get("Hourly Rate"),
              earnings = record.get("Earnings"),
              comment = record.get("Comment"),
              tags = readTagsFrom(record.get("Tags")),
              breaks = record.get("Breaks"),
              adjustments = record.get("Adjustments"),
              totalTimeAdjustment = record.get("TotalTimeAdjustment"),
              totalEarningsAdjustment = record.get("TotalEarningsAdjustment"),
              totalKilometrage = record.get("TotalKilometrage")
            ))
        .toList

      Right(Source.fromIterator(() => registrations.iterator))

    } catch {
      case e: Throwable => Left(e.getMessage)
    }
}
