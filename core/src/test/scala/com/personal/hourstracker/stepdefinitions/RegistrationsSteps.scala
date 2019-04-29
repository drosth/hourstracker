package com.personal.hourstracker.stepdefinitions

import java.text.SimpleDateFormat
import java.util.Locale

import akka.actor.ActorSystem
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.impl.DefaultRegistrationService
import com.personal.hourstracker.service.{ FacturationService, ImporterService, RegistrationService }
import cucumber.api.DataTable
import cucumber.api.scala.{ EN, ScalaDsl }
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfter, Matchers }
import org.slf4j.Logger

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

class RegistrationsSteps extends ScalaDsl with EN with Matchers with MockitoSugar with ScalaFutures {
  import Helpers._

  private implicit val logger: Logger = mock[Logger]
  private implicit val system: ActorSystem = ActorSystem()
  private implicit lazy val executionContext: ExecutionContext = system.dispatcher
  private implicit lazy val locale: Locale = new Locale("nl", "NL")

  private val registrationRepository: RegistrationRepository = mock[RegistrationRepository]
  private val importService: ImporterService = mock[ImporterService]
  private val facturationService: FacturationService = mock[FacturationService]
  private val registrationService: RegistrationService = new DefaultRegistrationService(registrationRepository, importService, facturationService)

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
        totalEarningsAdjustment = row.get("TotalEarningsAdjustment"))
    })
    when(importService.importRegistrationsFrom(fileName)).thenReturn(Future.successful(Right(fixture.toList)))
  }

  When("""^I import the registrations from file '(.*)'$""") { fileName: String =>
    RegistrationAttributes.registrations = List.empty
    Await.result(registrationService.importRegistrationsFrom(fileName), 3 seconds) match {
      case Right(numberOfRegistrationsImported) =>
        RegistrationAttributes.numberOfRegistrationsImported = numberOfRegistrationsImported

      case Left(e) => ???
    }
  }

  Then("""^my registrations consists of:$""") { expectedRegistrations: DataTable =>
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
        totalEarningsAdjustment = row.get("TotalEarningsAdjustment"))
    }) shouldEqual RegistrationAttributes.registrations
  }
}
