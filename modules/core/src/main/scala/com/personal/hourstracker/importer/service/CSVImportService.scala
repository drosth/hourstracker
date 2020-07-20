package com.personal.hourstracker.importer.service

import java.io.{File, FileInputStream, InputStreamReader, Reader}
import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Paths}

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

class CSVImportService()(implicit executionContext: ExecutionContext, mat: Materializer) extends ImporterService {

  import CSVImportService._

  private def importFrom(file: Path): Source[Registration, Future[IOResult]] = {
    FileIO
      .fromPath(file)
      .via(CsvParsing.lineScanner())
      .via(CsvToMap.toMapAsStrings(StandardCharsets.UTF_8))
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
            record.get("TotalEarningsAdjustment")
          )
      )
  }

  override def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]] = {
    importFrom(Paths.get(fileName))
      .alsoTo(Sink.foreach(it => println(it)))
      .runWith(Sink.seq)
      .map(records => Right(records.toList))
  }
}
