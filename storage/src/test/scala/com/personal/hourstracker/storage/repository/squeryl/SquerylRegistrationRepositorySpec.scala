package com.personal.hourstracker.storage.repository.squeryl

import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.storage.config.Configuration
import com.personal.hourstracker.storage.config.component.SquerylRegistrationRepositoryComponent
import com.personal.hourstracker.storage.repository.squeryl.converter.RegistrationConverters._
import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import com.personal.hourstracker.storage.repository.squeryl.schema.RegistrationSchema
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }

class SquerylRegistrationRepositorySpec
  extends FlatSpec
  with BeforeAndAfterAll
  with Matchers
  with MockitoSugar
  with SquerylRegistrationRepositoryComponent
  with Configuration {

  behavior of "SquerylRegistrationRepository"

  override protected def beforeAll(): Unit = {
    RegistrationSchema.reset()
    println("Reset database")
  }

  behavior of "find record by its identifier"

  it should "return nothing given registration does not exist" in {
    val id = 0L
    registrationRepository.findById(id) shouldEqual None
  }

  it should "return registration given persisted registration exists" in {
    val entity = Fixtures.DefaultRegistration
    val id = Fixtures.givenPersistedRegistration(entity).id

    registrationRepository.findById(id) shouldEqual Some(entity.convert())
  }

  behavior of "find all records"

  it should "return empty list given no registrations are present" in {
    registrationRepository.findAll() shouldEqual List.empty
  }

  it should "return list with one registration given single registration is persisted" in {
    val entity = Fixtures.DefaultRegistration
    Fixtures.givenPersistedRegistration(entity)

    registrationRepository.findAll() shouldEqual List(entity.convert())
  }

  object Fixtures {
    import org.squeryl.PrimitiveTypeMode._

    val DefaultRegistration: RegistrationEntity = RegistrationEntity(
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
