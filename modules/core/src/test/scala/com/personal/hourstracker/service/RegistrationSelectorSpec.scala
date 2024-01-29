package com.personal.hourstracker.service

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.service.RegistrationSelector.{RegistrationInYearSelector, RegistrationRangeSelector}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.Locale

class RegistrationSelectorSpec extends AnyFlatSpec with BeforeAndAfter with Matchers with MockitoSugar {

  implicit val locale: Locale = new Locale("nl", "NL")

  behavior of "RegistrationInYearSelector"

  it should "return correct list of Registrations, given year is 2019" in {
    val registrations = List(
      Fixtures.withRegistration(Some(1), Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(Some(5), Some("2020-01-01 00:00:00")))

    registrations.filter(RegistrationInYearSelector(2019).filter) shouldEqual List(
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-12-31 23:59:59")))
  }

  behavior of "RegistrationRangeSelector"

  it should "return empty list of Registrations, given startAt is undefined and endAt is undefined" in {
    val registrations = List(
      Fixtures.withRegistration(Some(1), Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(Some(5), Some("2020-01-01 00:00:00")))

    registrations.filter(RegistrationRangeSelector(None, None).filter) shouldEqual registrations
  }

  it should "return correct list of Registrations, given startAt is '2019-03-01' and endAt is undefined" in {
    val registrations = List(
      Fixtures.withRegistration(Some(1), Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(Some(5), Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(Some(6), Some("2020-01-01 00:00:00")))

    val classUnderTest = RegistrationRangeSelector(Some(LocalDate.of(2019, 3, 1)), None)

    registrations.filter(classUnderTest.filter) shouldEqual List(
      Fixtures.withRegistration(Some(4), Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(Some(5), Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(Some(6), Some("2020-01-01 00:00:00")))
  }

  it should "return correct list of Registrations, given startAt is undefined and endAt is '2019-03-01'" in {
    val registrations = List(
      Fixtures.withRegistration(Some(1), Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(Some(5), Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(Some(6), Some("2020-01-01 00:00:00")))

    val classUnderTest = RegistrationRangeSelector(None, Some(LocalDate.of(2019, 3, 1)))

    registrations.filter(classUnderTest.filter) shouldEqual List(
      Fixtures.withRegistration(Some(1), Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-03-01 00:00:00")))
  }

  it should "return correct list of Registrations, given startAt is '2019-01-01' and endAt is '2019-06-01'" in {
    val registrations = List(
      Fixtures.withRegistration(Some(1), Some("2018-12-31 23:59:59")),
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-03-01 00:00:00")),
      Fixtures.withRegistration(Some(5), Some("2019-12-31 23:59:59")),
      Fixtures.withRegistration(Some(6), Some("2020-01-01 00:00:00")))

    val classUnderTest = RegistrationRangeSelector(Some(LocalDate.of(2019, 1, 1)), Some(LocalDate.of(2019, 6, 1)))
    registrations.filter(classUnderTest.filter) shouldEqual List(
      Fixtures.withRegistration(Some(2), Some("2019-01-01 00:00:00")),
      Fixtures.withRegistration(Some(3), Some("2019-01-01 00:00:01")),
      Fixtures.withRegistration(Some(4), Some("2019-03-01 00:00:00")))
  }

  object Fixtures {
    lazy val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    implicit def toLocalDateTime(source: Option[String]): Option[LocalDateTime] =
      source.map(s => LocalDateTime.parse(s, dateTimeFormatter))

    val DefaultRegistrations: List[Registration] = List(
      withRegistration(Some(1), Some("2019-01-01 00:00:00")),
      withRegistration(Some(2), Some("2019-01-01 00:00:01")),
      withRegistration(Some(3), Some("2019-02-28 23:59:59")),
      withRegistration(Some(4), Some("2019-03-01 00:00:00")),
      withRegistration(Some(5), Some("2019-03-01 00:00:01")),
      withRegistration(Some(6), Some("2019-05-31 23:59:59")),
      withRegistration(Some(7), Some("2019-06-01 00:00:00")))

    def withRegistration(id: Option[Registration.RegistrationID], clockedIn: Option[String], clockedOut: Option[String] = None) =
      Registration(id, id.map(i => s"Job-$i").getOrElse("Job"), clockedIn, clockedOut)
  }
}
