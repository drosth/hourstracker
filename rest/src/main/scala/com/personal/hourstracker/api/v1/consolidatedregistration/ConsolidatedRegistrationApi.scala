package com.personal.hourstracker.api.v1.consolidatedregistration

import java.io.{ File, FileInputStream }
import java.nio.{ ByteBuffer, MappedByteBuffer }
import java.nio.channels.FileChannel
import java.nio.file.{ Path, Paths }
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.Try
import scala.util.control.NonFatal

import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpEntity.ChunkStreamPart
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.{ FileIO, Source }
import akka.util.{ ByteString, Timeout }
import com.personal.hourstracker.config.component.SystemComponent
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.ConsolidatedRegistration
import io.swagger.v3.oas.annotations.media.Schema
import scala.io
import scala.io.BufferedSource

import HttpMethods._
import akka.http.scaladsl.model.headers.RawHeader
import akka.io.IO
import akka.stream.IOResult

object ConsolidatedRegistrationApi {

  type ConsolidatedRegistrationModels = Seq[ConsolidatedRegistrationModel]
  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  implicit val askTimeout: Timeout = 500.millis

  @Schema
  final case class ConsolidatedRegistrationModel(date: LocalDate, job: String, duration: Option[Double], comment: Option[String])

  implicit val stringToLocalDateUnmarshaller: Unmarshaller[String, LocalDate] =
    Unmarshaller.strict[String, LocalDate] { LocalDate.parse(_, localDateFormatter) }

  object ModelAdapter {

    implicit def toModel(consolidatedRegistration: ConsolidatedRegistration): ConsolidatedRegistrationModel =
      ConsolidatedRegistrationModel(
        consolidatedRegistration.date,
        consolidatedRegistration.job,
        consolidatedRegistration.duration,
        consolidatedRegistration.comment)
  }
}

trait ConsolidatedRegistrationApi
  extends ConsolidatedRegistrationApiDoc
  with ConsolidatedRegistrationApiProtocol
  with SystemComponent
  with Configuration {

  import ConsolidatedRegistrationApi._

  lazy val consolidatedRegistrationRoutes: Route = getConsolidatedRegistrations

  val myEncodings = Seq(MediaRange(`application/pdf`), MediaRange(`application/json`))

  //  val AcceptJson = Accept(MediaRange(MediaTypes.`application/json`))

  def consolidatedRegistrationActor: ActorRef

  override def getConsolidatedRegistrations: Route =
    path("consolidated") {
      (get & extract(_.request.headers)) { requestHeaders =>
        println(s"requestHeaders: $requestHeaders")

        parameters("startAt".as[LocalDate].?, "endAt".as[LocalDate].?) {
          case (Some(startAt), endAt) =>
            println(s"(startAt, endAt) => ($startAt, $endAt)")
            complete(StatusCodes.OK)

          case _ =>
            val importFrom: Path = Paths.get("../core/target/dummy.pdf")
            println(s"importFrom: $importFrom")

            val b: BufferedSource = io.Source.fromFile(importFrom.toFile)

            //            val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(file)

            val headers = List(RawHeader("Content-Disposition", s"attachment; filename=hourstracker.pdf"))
            println(s"headers: $headers")

            //            io.Source.fromFile(Paths(file))

            val contentLength: Long = importFrom.toFile.length
            println(s"contentLength: $contentLength")

            val data: Source[ByteString, Any] = FileIO.fromPath(importFrom)

            respondWithHeaders(headers) {
              complete(HttpEntity(ContentTypes.`application/octet-stream`, contentLength, data))
            }

          //            complete(StatusCodes.OK)
        }
      }

      //          println(s"requestHeaders: $requestHeaders")
      //          println(s"query: $query")
      //
      //          val mediaTypeNegotiator = new MediaTypeNegotiator(requestHeaders)
      //
      //          val encoding: MediaRange = mediaTypeNegotiator.acceptedMediaRanges
      //            .intersect(myEncodings)
      //            .headOption
      //            .getOrElse(MediaRange(`application/json`))
      //
      //          println(s"encoding: $encoding")
      //
      //          complete(StatusCodes.OK)
    }
}
