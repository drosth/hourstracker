package com.personal.hourstracker.service

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import java.util.Locale

class RegistrationSelectorSpec extends AnyFlatSpec with BeforeAndAfter with should.Matchers {

  implicit val locale: Locale = new Locale("nl", "NL")

  behavior of "RegistrationInYearSelector"

  it should "return correct list of Registrations, given year is 2019" in {
    val registrations = List(
      Fixtures.withRegistration(1, Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(5, Some("2020-01-01 00:00:00")))

    registrations.filter(RegistrationInYearSelector(2019).filter) shouldEqual List(
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-12-31 23:59:59")))
  }

  behavior of "RegistrationRangeSelector"

  it should "return empty list of Registrations, given startAt is undefined and endAt is undefined" in {
    val registrations = List(
      Fixtures.withRegistration(1, Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(5, Some("2020-01-01 00:00:00")))

    registrations.filter(RegistrationRangeSelector(None, None).filter) shouldEqual registrations
  }

  it should "return correct list of Registrations, given startAt is '2019-03-01' and endAt is undefined" in {
    val registrations = List(
      Fixtures.withRegistration(1, Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(5, Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(6, Some("2020-01-01 00:00:00")))

    val classUnderTest = RegistrationRangeSelector(Some(LocalDate.of(2019, 3, 1)), None)

    registrations.filter(classUnderTest.filter) shouldEqual List(
      Fixtures.withRegistration(4, Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(5, Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(6, Some("2020-01-01 00:00:00")))
  }

  it should "return correct list of Registrations, given startAt is undefined and endAt is '2019-03-01'" in {
    val registrations = List(
      Fixtures.withRegistration(1, Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(5, Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(6, Some("2020-01-01 00:00:00")))

    val classUnderTest = RegistrationRangeSelector(None, Some(LocalDate.of(2019, 3, 1)))

    registrations.filter(classUnderTest.filter) shouldEqual List(
      Fixtures.withRegistration(1, Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-03-01 00:00:00")))
  }

  it should "return correct list of Registrations, given startAt is '2019-01-01' and endAt is '2019-06-01'" in {
    val registrations = List(
      Fixtures.withRegistration(1, Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(5, Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(6, Some("2020-01-01 00:00:00")))

    val classUnderTest = RegistrationRangeSelector(Some(LocalDate.of(2019, 1, 1)), Some(LocalDate.of(2019, 6, 1)))
    registrations.filter(classUnderTest.filter) shouldEqual List(
      Fixtures.withRegistration(2, Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(3, Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(4, Some("2019-03-01 00:00:00")))
  }

  object Fixtures {
    lazy val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    implicit def toLocalDateTime(source: Option[String]): Option[LocalDateTime] =
      source.map(s => LocalDateTime.parse(s, dateTimeFormatter))

    val DefaultRegistrations: List[Registration] = List(
      withRegistration(1, Some("2019-01-01 00:00:00")),
      withRegistration(2, Some("2019-01-01 00:00:01")),
      withRegistration(3, Some("2019-02-28 23:59:59")),
      withRegistration(4, Some("2019-03-01 00:00:00")),
      withRegistration(5, Some("2019-03-01 00:00:01")),
      withRegistration(6, Some("2019-05-31 23:59:59")),
      withRegistration(7, Some("2019-06-01 00:00:00")))

    def withRegistration(id: Int, clockedIn: Option[String], clockedOut: Option[String] = None) =
      Registration(Some(id), s"Job-$id", clockedIn, clockedOut)
  }

}
