package com.personal.hourstracker.storage.repository.squeryl

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.config.component.SquerylRegistrationRepositoryComponent
import com.personal.hourstracker.storage.repository.squeryl.converter.RegistrationConverter._
import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import com.personal.hourstracker.storage.repository.squeryl.schema.RegistrationSchema
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfterAll, Matchers, Outcome, fixture }

import scala.concurrent.ExecutionContext

class SquerylRegistrationRepositorySpec
  extends fixture.FlatSpec
  with BeforeAndAfterAll
  with Matchers
  with MockitoSugar
  with SquerylRegistrationRepositoryComponent
  with StorageConfiguration {

  import org.squeryl.PrimitiveTypeMode._

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  override type FixtureParam = RegistrationRepository

  override def withFixture(test: OneArgTest): Outcome = {
    startDatabaseSession()
    transaction {
      RegistrationSchema.drop
      RegistrationSchema.create
    }
    test(registrationRepository)
  }

  behavior of "SquerylRegistrationRepository"

  behavior of "saving a registration"

  it should "return the ID of the persisted registration" in { classUnderTest =>
    classUnderTest.save(Fixtures.DefaultRegistration) match {
      case Left(_) => fail("Expected persisted ID")
      case Right(id) => id shouldNot be(0)
    }
  }

  it should "persist registration given registration to persist has no ID" in { classUnderTest =>
    val registration = Fixtures.DefaultRegistration.copy(id = None)

    classUnderTest.save(registration) match {
      case Left(_) => fail("Expected persisted ID")
      case Right(id) =>
        val expectedRegistration = registration.copy(id = Some(id))
        inTransaction {
          RegistrationSchema.registrations.lookup(id).map(_.convert) shouldEqual Some(expectedRegistration)
        }
    }
  }

  it should "persist registration given registration to persist has ID and cannot be found in storage" in { classUnderTest =>
    val registration = Fixtures.DefaultRegistration.copy(id = Some(999L))

    classUnderTest.save(registration) match {
      case Left(_) => fail("Expected persisted ID")
      case Right(id) =>
        id shouldNot be(999L)

        val expectedRegistration = registration.copy(id = Some(id))
        inTransaction {
          RegistrationSchema.registrations.lookup(id).map(_.convert) shouldEqual Some(expectedRegistration)
        }
    }
  }

  it should "update a record when registration can be found in storage" in { classUnderTest =>
    val insertedEntity = Fixtures.givenPersistedRegistration(Fixtures.DefaultRegistrationEntity)
    val registration = insertedEntity.convert.copy(hourlyRate = Some("2.3".toDouble))

    classUnderTest.save(registration) match {
      case Left(_) => fail("Expected persisted ID")
      case Right(id) =>
        id shouldEqual insertedEntity.id
        inTransaction {
          RegistrationSchema.registrations.lookup(id).map(_.convert) shouldEqual Some(registration)
        }
    }
  }

  behavior of "find record by its identifier"

  it should "return nothing given registration does not exist" in { classUnderTest =>
    val id = 0L
    classUnderTest.findById(id) shouldEqual None
  }

  it should "return registration given persisted registration exists" in { classUnderTest =>
    val entity = Fixtures.DefaultRegistrationEntity
    val id = Fixtures.givenPersistedRegistration(entity).id

    classUnderTest.findById(id) shouldEqual Some(entity.convert)
  }

  behavior of "find all records"

  it should "return empty list given no registrations are present" in { classUnderTest =>
    classUnderTest.findAll().runWith(TestSink.probe[Registration]).request(1)
      .expectComplete()
  }

  it should "return list with one registration given single registration is persisted" in { classUnderTest =>
    val entity = Fixtures.DefaultRegistrationEntity
    Fixtures.givenPersistedRegistration(entity)

    classUnderTest.findAll().runWith(TestSink.probe[Registration]).request(1)
      .expectNext(entity.convert)
      .expectComplete()
  }

  object Fixtures {

    val DefaultRegistration: Registration = Registration(
      id = None,
      job = "job",
      clockedIn = Some(LocalDateTime.of(2019, 1, 2, 3, 4, 5)),
      clockedOut = Some(LocalDateTime.of(2019, 1, 2, 4, 5, 6)),
      hourlyRate = Some("1.2".toDouble),
      earnings = Some("2.3".toDouble),
      comment = Some("comment"),
      tags = None,
      totalTimeAdjustment = Some("1.2".toDouble),
      totalEarningsAdjustment = Some("1.2".toDouble))

    val DefaultRegistrationEntity: RegistrationEntity = RegistrationEntity(
      job = "job",
      clockedIn = Some(Timestamp.valueOf(LocalDateTime.of(2019, 1, 2, 3, 4, 5))),
      clockedOut = Some(Timestamp.valueOf(LocalDateTime.of(2019, 1, 2, 4, 5, 6))),
      hourlyRate = Some("1.2".toDouble),
      earnings = Some("2.3".toDouble),
      comment = Some("comment"),
      tags = None,
      totalTimeAdjustment = Some("1.2".toDouble),
      totalEarningsAdjustment = Some("1.2".toDouble))

    def givenPersistedRegistration(registration: RegistrationEntity): RegistrationEntity = {
      inTransaction {
        RegistrationSchema.registrations.insert(registration)
      }
    }
  }
}
