package com.personal.hourstracker.api.v1.registration

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.model.StatusCodes
import com.personal.hourstracker.api.v1.ApiSpec
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.config.component._
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.{ ImporterService, RegistrationService }
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._

import scala.concurrent.Future

class RegistrationApiSpec
  extends ApiSpec
  with RegistrationApi
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
    givenSomeRegistrations()

    Get("/registration") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(
        registrationInLastDayOfPreviousMonth,
        registrationInFirstDayOfCurrentMonth,
        registrationInLastDayOfCurrentMonth,
        registrationInFirstDayOfNextMonth).map(_.convert())
    }
  }

  it should "be able retrieve registrations before a boundary" in {
    givenSomeRegistrations()

    val endAt = registrationInFirstDayOfCurrentMonth.clockedIn.get.format(formatter) // 2019-04-01
    Get(s"/registration?endAt=$endAt") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(registrationInLastDayOfPreviousMonth, registrationInFirstDayOfCurrentMonth).map(
        _.convert())
    }
  }

  it should "be able retrieve registrations after a boundary" in {
    givenSomeRegistrations()

    val startAt = registrationInFirstDayOfCurrentMonth.clockedIn.get.format(formatter)
    Get(s"/registration?startAt=$startAt") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(
        registrationInFirstDayOfCurrentMonth,
        registrationInLastDayOfCurrentMonth,
        registrationInFirstDayOfNextMonth).map(_.convert())
    }
  }

  it should "be able retrieve registrations of between two boundaries" in {
    givenSomeRegistrations()

    val startAt = LocalDateTime.now().withDayOfMonth(1).format(formatter)
    val endAt = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).minusDays(1).format(formatter)
    Get(s"/registration?startAt=$startAt&endAt=$endAt") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(registrationInFirstDayOfCurrentMonth, registrationInLastDayOfCurrentMonth).map(
        _.convert())
    }
  }

  it should "be able retrieve registrations of a year" in {
    givenSomeRegistrationsOverYears()

    val year = LocalDateTime.now().getYear
    Get(s"/registration/$year") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(registrationInFirstDayOfCurrentYear, registrationInLastDayOfCurrentYear).map(
        _.convert())
    }
  }

  it should "be able retrieve registrations in a month of a year" in {
    when(registrationService.fetchRegistrations())
      .thenReturn(Future.successful(registrationsWithMonthlyBoundaries ::: registrationsWithYearlyBoundaries))

    val year = LocalDateTime.now().getYear
    val month = LocalDateTime.now().getMonthValue

    Get(s"/registration/$year/$month") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(
        registrationInFirstDayOfCurrentMonth,
        registrationInLastDayOfCurrentMonth).map(_.convert())
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

    def givenSomeRegistrations() =
      when(registrationService.fetchRegistrations()).thenReturn(Future.successful(registrationsWithMonthlyBoundaries))

    val registrationsWithYearlyBoundaries =
      List(
        registrationInLastDayOfPreviousYear,
        registrationInFirstDayOfCurrentYear,
        registrationInLastDayOfCurrentYear,
        registrationInFirstDayOfNextYear)

    def givenSomeRegistrationsOverYears() =
      when(registrationService.fetchRegistrations()).thenReturn(Future.successful(registrationsWithYearlyBoundaries))

    def givenImportingRegistrationsIsSuccessful() =
      when(registrationService.importRegistrationsFrom(any[String])).thenReturn(Future.successful(Right(4)))
  }
}
