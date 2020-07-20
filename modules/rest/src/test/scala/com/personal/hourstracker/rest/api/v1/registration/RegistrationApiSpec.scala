package com.personal.hourstracker.rest.api.v1.registration

import java.time.LocalDateTime

class RegistrationApiSpec
  extends ApiSpec
    with RegistrationApi
    with RegistrationModule
    with MockitoSugar
    with ImporterModule
    with PresenterModule
    with LoggingComponent
    with SystemComponent
    with ScalatestRouteTest {

  import Fixtures._

  override lazy val importerService: ImporterService = mock[ImporterService]
  override lazy val registrationRepository: RegistrationRepository = mock[RegistrationRepository]
  override lazy val registrationService: RegistrationService = mock[RegistrationService]

  behavior of "Importing registrations"

  it should "be able to import registrations" in {
    givenImportingRegistrationsIsSuccessful()

    Get(s"/registrations/import") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(
        registrationInLastDayOfPreviousMonth,
        registrationInFirstDayOfCurrentMonth,
        registrationInLastDayOfCurrentMonth,
        registrationInFirstDayOfNextMonth
      ).map(_.convert())
    }
  }

  behavior of "Fetching registrations"

  it should "be able retrieve all registrations" in {
    givenFetchingRegistrationsReturns(registrationsWithMonthlyBoundaries)

    Get("/registrations") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Seq[RegistrationModel]] shouldBe Seq(
        registrationInLastDayOfPreviousMonth,
        registrationInFirstDayOfCurrentMonth,
        registrationInLastDayOfCurrentMonth,
        registrationInFirstDayOfNextMonth
      ).map(_.convert())
    }
  }

  it should "be able retrieve registrations of a year" in {
    val year = LocalDateTime.now().getYear
    givenFetchingRegistrationsOfAYearReturns(year, registrationsWithYearlyBoundaries)

    Get(s"/registrations/$year") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[List[RegistrationModel]] shouldBe registrationsWithYearlyBoundaries.map(_.convert())
    }
  }

  it should "be able retrieve registrations in a month of a year" in {
    val year = LocalDateTime.now().getYear
    val month = LocalDateTime.now().getMonthValue
    val registrations = registrationsWithMonthlyBoundaries ::: registrationsWithYearlyBoundaries
    givenFetchingRegistrationsOfAMonthInAYearReturns(year, month, registrations)

    Get(s"/registrations/$year/$month") ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[List[RegistrationModel]] shouldBe registrations.map(_.convert())
    }
  }

  behavior of "Uploading registrations"

  it should "behave" in {
    givenImportingRegistrationsIsSuccessful()

    val multipartForm =
      Multipart.FormData(
        Multipart.FormData.BodyPart.Strict(
          "csv",
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            //          "2,3,5\n7,11,13,17,23\n29,31,37\n"
            """sep=,
              |"Job","Clocked In","Clocked Out","Duration","Hourly Rate","Earnings","Comment","Tags","Breaks","Adjustments","TotalTimeAdjustment","TotalEarningsAdjustment"
              |"Johan Enschedé","14/11/2011 10:45","14/11/2011 17:49","7","74,38","520,66","","","","","",""
              |""".stripMargin
          ),
          Map("filename" -> "exported.csv")
        )
      )

    Post("/registrations/upload", multipartForm) ~> registrationRoutes ~> check {
      status shouldEqual StatusCodes.OK
      //      responseAs[String] shouldEqual "Sum: 178"
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
        registrationInFirstDayOfNextMonth
      )

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
        registrationInFirstDayOfNextYear
      )

    def givenImportingRegistrationsIsSuccessful() = {
      val foo: List[Registration] = registrationsWithMonthlyBoundaries

      val result: Source[Either[String, Registrations], NotUsed] = Source.single(Right(foo))

      when(registrationService.importRegistrationsFromSource(any[String])).thenReturn(
        result
      )
    }
  }

}
