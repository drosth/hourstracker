package com.personal.hourstracker.storage.repository

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.actor.ActorSystem
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{ SquerylRegistrationRepositoryComponent, SystemComponent }
import com.personal.hourstracker.storage.domain.RegistrationModel
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfter, Matchers, Outcome, _ }
import org.squeryl.PrimitiveTypeMode._

import scala.concurrent.ExecutionContext

class RegistrationSquerylRepositorySpec
  extends fixture.FlatSpec
  with BeforeAndAfter
  with Matchers
  with MockitoSugar
  with Configuration
  with SquerylRegistrationRepositoryComponent
  with SystemComponent {

  override val system: ActorSystem = ActorSystem()
  override val executionContext: ExecutionContext = system.dispatcher

  type FixtureParam = RegistrationRepository

  override protected def withFixture(test: OneArgTest): Outcome = {
    test(registrationRepository)
  }

  behavior of "MySQLRegistrationRepository with Squeryl"

  behavior of "save"

  it should "insert registration into table when registration is unknown" in { repository =>
  }

  it should "update registration into table when registration can be found" in { repository =>
    //    inTransaction {
    //      val defaultRegistration = repository.registrations.insert(Fixtures.DefaultRegistration)
    //
    //      val selectedRegistration =
    //        from(repository.registrations)(registration => where(registration.id === defaultRegistration.id) select (registration))
    //
    //      selectedRegistration shouldEqual (defaultRegistration)
    //    }
  }

  behavior of "findById"

  it should "return registration from table when id can be found" in { repository =>
    inTransaction {
      val defaultRegistration = registrationSchema.registrations.insert(Fixtures.DefaultRegistration)

      val actual = repository.findById(defaultRegistration.id)

      actual shouldEqual defaultRegistration
    }
  }

  it should "return nothing when id cannot be found" in { repository =>
    val actual = repository.findById(Long.MaxValue)

    actual shouldEqual None
  }

  object Fixtures {

    final val DefaultRegistration = RegistrationModel(
      job = "Job",
      clockedIn = Some(Timestamp.valueOf(LocalDateTime.now.minusDays(1))),
      clockedOut = Some(Timestamp.valueOf(LocalDateTime.now)),
      duration = Some("1.2".toDouble),
      hourlyRate = Some("2.34".toDouble),
      earnings = Some("3.4".toDouble),
      comment = Some("Comment"),
      //    tags: Option[String] = None,
      totalTimeAdjustment = Some("4.56".toDouble),
      totalEarningsAdjustment = Some("5.6".toDouble))
  }
}
