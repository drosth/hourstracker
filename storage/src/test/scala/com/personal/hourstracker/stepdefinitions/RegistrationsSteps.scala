package com.personal.hourstracker.stepdefinitions

import java.text.SimpleDateFormat

import akka.actor.ActorSystem
import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import com.personal.hourstracker.service.impl.DefaultRegistrationService
import com.personal.hourstracker.service.{ ImporterService, RegistrationService }
import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.config.component.SquerylRegistrationRepositoryComponent
import cucumber.api.DataTable
import cucumber.api.scala.{ EN, ScalaDsl }
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.slf4j.Logger

import scala.concurrent.ExecutionContext

class RegistrationsSteps
  extends ScalaDsl
  with EN
  with Matchers
  with MockitoSugar
  with ScalaFutures
  with SquerylRegistrationRepositoryComponent
  with StorageConfiguration {

  import Helpers._

  implicit val logger: Logger = mock[Logger]
  implicit val system: ActorSystem = ActorSystem()
  implicit lazy val executionContext: ExecutionContext = system.dispatcher

  implicit val searchParameters: SearchParameters = SearchParameters.UndefinedSearchParameters

  val importService: ImporterService = mock[ImporterService]
  val registrationService: RegistrationService = new DefaultRegistrationService(importService)

  Then("""^my storage contains:$""") { expectedRegistrations: DataTable =>
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
