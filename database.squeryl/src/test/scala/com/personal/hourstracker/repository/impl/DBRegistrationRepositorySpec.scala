package com.personal.hourstracker.repository.impl

import java.time.LocalDateTime

import scala.concurrent.Await
import scala.concurrent.duration._

import com.personal.hourstracker.config.component.SquerylComponentForMySQL
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.{Registration, SearchParameters}
import com.personal.hourstracker.repository.{HourstrackerDB, _}
import org.scalatest.{fixture, BeforeAndAfterAll, Matchers, Outcome}
import org.scalatest.mockito.MockitoSugar
import org.squeryl.Session

class DBRegistrationRepositorySpec
    extends fixture.FlatSpec
    with BeforeAndAfterAll
    with Matchers
    with MockitoSugar
    with SquerylComponentForMySQL
    with Configuration {

  databaseSession.start()
  Session.create().

  private implicit val searchParameters: SearchParameters = SearchParameters.UndefinedSearchParameters
  private lazy val schema: HourstrackerDB = HourstrackerDB()
  private lazy val testData = new TestData(schema)

  val classUnderTest: DBRegistrationRepository = new DBRegistrationRepository(databaseSession)

  type FixtureParam = DBRegistrationRepository

  override def withFixture(test: OneArgTest): Outcome = {
    try test(new DBRegistrationRepository(databaseSession))
    finally {
      // Shared cleanup (run at end of each test)
    }
  }

  override protected def beforeAll() = {
    import schema._

    transaction {
      new TestData(schema)
    }
  }

  behavior of "DBRegistrationRepository"

  behavior of "loading registrations"

  it should "behave" in { classUnderTest =>
    import testData._
    val actual = Await.result(classUnderTest.load(), 3 seconds)

    actual should contain(job1)
  }

  class TestData(schema: HourstrackerDB) {
    import schema._

    val job1 = registrations.insert(
      Registration(
        job = "Job-1",
        clockedIn = Some(LocalDateTime.now().minusHours(8)),
        clockedOut = Some(LocalDateTime.now()),
        duration = Some(8)
//    hourlyRate: Option[Double] = None,
//    earnings: Option[Double] = None,
//    comment: Option[String] = None,
//    tags: Option[Set[String]] = None,
//    breaks: Option[String] = None,
//    adjustments: Option[String] = None,
//    totalTimeAdjustment: Option[Double] = None,
//    totalEarningsAdjustment: Option[String] = None
      )
    )
  }
}
