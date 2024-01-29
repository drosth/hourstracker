package com.personal.hourstracker.importer.service

import java.io.{ File, FileInputStream, InputStreamReader, Reader }

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.github.tototoshi.csv.CSVReader
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.{ ImporterService, _ }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

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

      Right(Source.fromIterator(() => registrations.iterator))

    } catch {
      case e: Throwable => Left(e.getMessage)
    }
}
