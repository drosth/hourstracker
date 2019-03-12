package com.personal.hourstracker.stepdefinitions

import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField
import java.util.Date

import akka.actor.ActorSystem
import com.personal.hourstracker.domain.{Registration, SearchParameters}
import com.personal.hourstracker.service.impl.DefaultRegistrationService
import com.personal.hourstracker.service.{ImporterService, RegistrationService}
import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.slf4j.Logger

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object Helpers {
  implicit def optionToOption[A](source: Option[String]): Option[A] = source.map(_.asInstanceOf[A])

  implicit def toLocalDateTime(source: Date): LocalDateTime = LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault())

  implicit class DataTableToListOfMaps(dataTable: DataTable) {

    def withMaps[A](f: Map[String, String] => A): List[A] = {
      dataTable
        .asMaps(classOf[String], classOf[String])
        .asScala
        .toList
        .map(row => row.asScala.toMap)
        .map(row => f(row))
    }
  }
}

class RegistrationsSteps extends ScalaDsl with EN with Matchers with MockitoSugar with ScalaFutures {
  import Helpers._

  implicit val logger: Logger = mock[Logger]
  implicit val system: ActorSystem = ActorSystem()
  implicit lazy val executionContext: ExecutionContext = system.dispatcher

  implicit val searchParameters: SearchParameters = SearchParameters.UndefinedSearchParameters

  val importService: ImporterService = mock[ImporterService]
  val registrationService: RegistrationService = new DefaultRegistrationService(importService)

  Given("""^a CSV file named '(.*)' with the following registrations:$""") { (fileName: String, dataTable: DataTable) =>
    val dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm")
    val fixture: Seq[Registration] = dataTable withMaps (row => {
      Registration(
        id = None,
        job = row.getOrElse("Job", ""),
        clockedIn = row.get("Clocked In").map(source => { dateTimeFormatter.parse(source) }),
        clockedOut = row.get("Clocked Out").map(source => { dateTimeFormatter.parse(source) }),
        duration = row.get("Duration"),
        hourlyRate = row.get("Hourly Rate"),
        earnings = row.get("Earnings"),
        comment = row.get("Comment"),
        tags = row.get("Tags").map(_.split(";").toSet),
        totalTimeAdjustment = row.get("TotalTimeAdjustment"),
        totalEarningsAdjustment = row.get("TotalEarningsAdjustment")
      )
    })
    when(importService.importRegistrationsFrom(fileName)).thenReturn(Future.successful(Right(fixture.toList)))
  }

  When("""^I import the registrations from file '(.*)'$""") { fileName: String =>
    RegistrationAttributes.registrations = List.empty
    Await.result(registrationService.importRegistrationsFrom(fileName), 3 seconds) match {
      case Right(r) =>
        RegistrationAttributes.registrations = r

      case Left(e) => ???
    }
  }

  Then("""^my registrations must contain:$""") { expectedRegistrations: DataTable =>
    val dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm")
    expectedRegistrations withMaps (row => {
      Registration(
        id = None,
        job = row.getOrElse("Job", ""),
        clockedIn = row
          .get("Clocked In")
          .map(source => {
            dateTimeFormatter.parse(source)
          }),
        clockedOut = row
          .get("Clocked Out")
          .map(source => {
            dateTimeFormatter.parse(source)
          }),
        duration = row.get("Duration"),
        hourlyRate = row.get("Hourly Rate"),
        earnings = row.get("Earnings"),
        comment = row.get("Comment"),
        tags = row.get("Tags").map(_.split(",").map(_.trim).toSet),
        totalTimeAdjustment = row.get("TotalTimeAdjustment"),
        totalEarningsAdjustment = row.get("TotalEarningsAdjustment")
      )
    }) shouldEqual RegistrationAttributes.registrations
  }
}
