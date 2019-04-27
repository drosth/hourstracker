package com.personal.hourstracker.api.v1.registration

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.model.StatusCodes
import akka.stream.scaladsl.Source
import com.personal.hourstracker.api.v1.ApiSpec
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.config.component._
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.RegistrationService.{ SelectByYear, SelectByYearAndMonth }
import com.personal.hourstracker.service.presenter.ConsolidatedRegistrationsPdfPresenter
import com.personal.hourstracker.service.presenter.config.HtmlPresenterComponent
import com.personal.hourstracker.service.{ ImporterService, RegistrationService }
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._

import scala.concurrent.Future

class RegistrationApiSpec
  extends ApiSpec
  with RegistrationApi
  with FacturationComponent with ConsolidatedRegistrationsPdfPresenter with HtmlPresenterComponent
  with RegistrationComponent
  with RegistrationRepositoryComponent
  with ImporterServiceComponent
  with LoggingComponent
  with SystemComponent {

  import Fixtures._
  import RegistrationApi._

  override lazy val importerService: ImporterService = mock[ImporterService]
  override lazy val registrationRepository: RegistrationRepository = mock[RegistrationRepository]
  override lazy val registrationService: RegistrationService = mock[RegistrationService]

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  behavior of "RegistrationApi"

  behavior of "Fetching registrations"

  it should "be able retrieve all registrations" in {
    givenFetchingRegistrationsReturns(registrationsWithMonthlyBoundaries)

    Get("/registration") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(
        registrationInLastDayOfPreviousMonth,
        registrationInFirstDayOfCurrentMonth,
        registrationInLastDayOfCurrentMonth,
        registrationInFirstDayOfNextMonth).map(_.convert())
    }
  }

  it should "be able retrieve registrations of a year" in {
    val year = LocalDateTime.now().getYear
    givenFetchingRegistrationsOfAYearReturns(year, registrationsWithYearlyBoundaries)

    Get(s"/registration/$year") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[List[RegistrationModel]] shouldBe registrationsWithYearlyBoundaries.map(_.convert())
    }
  }

  it should "be able retrieve registrations in a month of a year" in {
    val year = LocalDateTime.now().getYear
    val month = LocalDateTime.now().getMonthValue
    val registrations = registrationsWithMonthlyBoundaries ::: registrationsWithYearlyBoundaries
    givenFetchingRegistrationsOfAMonthInAYearReturns(year, month, registrations)

    Get(s"/registration/$year/$month") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[List[RegistrationModel]] shouldBe registrations.map(_.convert())
    }
  }

  behavior of "Importing registrations"

  it should "be able to import registrations" in {
    givenImportingRegistrationsIsSuccessful()

    Post(s"/registration/import") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.Accepted
    }
  }

  object Fixtures {

    val registrationInLastDayOfPreviousYear =
      Registration(job = "Last day of previous year", clockedIn = Some(LocalDateTime.now().withDayOfYear(1).minusDays(1)))

    val registrationInFirstDayOfCurrentYear =
      Registration(job = "First day of current year", clockedIn = Some(LocalDateTime.now().withDayOfYear(1)))

    val registrationInLastDayOfPreviousMonth =
      Registration(job = "Last day of previous month", clockedIn = Some(LocalDateTime.now().withDayOfMonth(1).minusDays(1)))

    val registrationInFirstDayOfCurrentMonth =
      Registration(job = "First day of current month", clockedIn = Some(LocalDateTime.now().withDayOfMonth(1)))

    val registrationInLastDayOfCurrentMonth =
      Registration(job = "Last day of current month", clockedIn = Some(LocalDateTime.now().plusMonths(1).withDayOfMonth(1).minusDays(1)))

    val registrationInFirstDayOfNextMonth =
      Registration(job = "First day of next month", clockedIn = Some(LocalDateTime.now().plusMonths(1).withDayOfMonth(1)))

    val registrationInLastDayOfCurrentYear =
      Registration(job = "Last day of current year", clockedIn = Some(LocalDateTime.now().plusYears(1).withDayOfYear(1).minusDays(1)))

    val registrationInFirstDayOfNextYear =
      Registration(job = "First day of next year", clockedIn = Some(LocalDateTime.now().plusYears(1).withDayOfYear(1)))

    val registrationsWithMonthlyBoundaries =
      List(
        registrationInLastDayOfPreviousMonth,
        registrationInFirstDayOfCurrentMonth,
        registrationInLastDayOfCurrentMonth,
        registrationInFirstDayOfNextMonth)

    def givenFetchingRegistrationsReturns(registrations: Registrations = List.empty) =
      when(registrationService.fetchRegistrations()).thenReturn(Source.fromIterator(() => registrations.iterator))

    def givenFetchingRegistrationsOfAYearReturns(year: Int, registrations: Registrations = List.empty) =
      when(registrationService.fetchRegistrations(SelectByYear(year))).thenReturn(Source.fromIterator(() => registrations.iterator))

    def givenFetchingRegistrationsOfAMonthInAYearReturns(year: Int, month: Int, registrations: Registrations = List.empty) =
      when(registrationService.fetchRegistrations(SelectByYearAndMonth(year, month)))
        .thenReturn(Source.fromIterator(() => registrations.iterator))

    val registrationsWithYearlyBoundaries =
      List(
        registrationInLastDayOfPreviousYear,
        registrationInFirstDayOfCurrentYear,
        registrationInLastDayOfCurrentYear,
        registrationInFirstDayOfNextYear)

    def givenImportingRegistrationsIsSuccessful() =
      when(registrationService.importRegistrationsFrom(any[String])).thenReturn(Future.successful(Right(4)))
  }
}
