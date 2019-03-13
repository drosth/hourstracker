package com.personal.hourstracker.repository

import java.time.LocalDateTime

import akka.actor.ActorSystem
import com.personal.hourstracker.config.component.H2Component
import com.personal.hourstracker.domain.Registration
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ fixture, BeforeAndAfter, Matchers }
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.{ ConnectionPool, DB, DBSession, _ }

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext }
import scala.util.Random

class MySQLRegistrationRepositorySpec
  extends fixture.FlatSpec
  with AutoRollback
  with BeforeAndAfter
  with Matchers
  with MockitoSugar
  with H2Component {

  implicit val system: ActorSystem = ActorSystem()
  implicit lazy val executionContext: ExecutionContext = system.dispatcher

  private final val RegistrationId: Registration.RegistrationID = Random.nextLong()
  private val classUnderTest: MySQLRegistrationRepository = new MySQLRegistrationRepository

  //  override def fixture(implicit session: DBSession): Unit = {
  //    classUnderTest.initialize()
  //  }

  behavior of "MySQLRegistrationRepository"

  behavior of "save"

  it should "insert registration into table when registration is unknown" in { implicit session =>
  }

  it should "update registration into table when registration can be found" in { implicit session =>
    val registrationId: Registration.RegistrationID = Random.nextLong()

    val registration = Fixtures.DefaultRegistration.copy(id = Some(registrationId))
    Fixtures.givenRegistration(registration)

    val updatedRegistration = registration.copy(comment = Some("Updated registration"))
    Await.result(classUnderTest.save(updatedRegistration), 2 seconds) shouldEqual Right(registration.id.get)

    sql"SELECT * FROM Registration WHERE id = ${registration.id}"
      .map(RegistrationMapper.toRegistration)
      .single
      .apply()
      .shouldBe(Some(updatedRegistration))
  }

  behavior of "findById"

  it should "return registration from table when id can be found" in { implicit session =>
    val registrationId: Registration.RegistrationID = Random.nextLong()
    val registration = Fixtures.DefaultRegistration.copy(id = Some(registrationId))

    Fixtures.givenRegistration(Fixtures.DefaultRegistration.copy(id = Some(registrationId)))

    Await.result(classUnderTest.findById(registrationId), 2 seconds) shouldEqual Some(registration)
  }

  it should "return nothing when id cannot be found" in { implicit session =>
    Await.result(classUnderTest.findById(-1), 2 seconds) shouldEqual None
  }

  object Fixtures {

    import scalikejdbc._

    val DefaultRegistration = Registration(
      id = None,
      job = "Job",
      clockedIn = Some(LocalDateTime.now().minusDays(1)),
      clockedOut = Some(LocalDateTime.now()),
      duration = Some(1.23),
      hourlyRate = Some(2.34),
      earnings = Some(3.45),
      comment = Some("Comment"),
      tags = Some(Set("tag-1", "tag-2")),
      totalTimeAdjustment = Some(4.56),
      totalEarningsAdjustment = Some(5.67))

    def givenRegistration(registration: Registration = DefaultRegistration): Long = {
      sql"""
        INSERT INTO Registration (
          id,
          job,
          clocked_in,
          clocked_out,
          duration,
          hourly_rate,
          earnings,
          comment,
          tags,
          total_time_adjustment,
          total_earnings_adjustment
        ) VALUES (
          ${registration.id},
          ${registration.job},
          ${registration.clockedIn},
          ${registration.clockedOut},
          ${registration.duration},
          ${registration.hourlyRate},
          ${registration.earnings},
          ${registration.comment},
          ${registration.tags.map(_.mkString(";"))},
          ${registration.totalTimeAdjustment},
          ${registration.totalEarningsAdjustment}
        )
     """.update().apply()
    }
  }

}
