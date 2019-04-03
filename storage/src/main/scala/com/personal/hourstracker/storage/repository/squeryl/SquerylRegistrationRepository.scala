package com.personal.hourstracker.storage.repository.squeryl

import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import com.personal.hourstracker.storage.repository.squeryl.schema.RegistrationSchema
import org.slf4j.{ Logger, LoggerFactory }
import org.squeryl.PrimitiveTypeMode.{ transaction, _ }

class SquerylRegistrationRepository extends RegistrationRepository {
  import RegistrationSchema._
  import com.personal.hourstracker.storage.repository.squeryl.converter.RegistrationConverter._

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[RegistrationRepository])

  def initialize(): Unit = {
    transaction {
      RegistrationSchema.initialize()
      RegistrationSchema.printDdl(message => logger.debug(message))
    }
  }

  override def findAll(): List[Registration] = transaction {
    registrations.toList.map(_.convert)
  }

  override def save(registration: Registration): Either[String, Long] = transaction {
    logger.info("Saving registration")

    val found: Seq[Registration] = findBy(registration.job, registration.clockedIn, registration.clockedOut)
    if (found.isEmpty) {
      logger.debug(s"Did not find registration with job: '${registration.job}', clockedIn: '${registration.clockedIn}', clockedOut: '${registration.clockedOut}'")
      Right(registrations.insert(registration.copy(id = None).convert).id)
    } else {
      updateRegistration(registration)
    }
  }

  private def updateRegistration(registration: Registration): Either[String, Long] = {
    def updateEntity(id: Long, entity: RegistrationEntity): Long = transaction {
      update(registrations)(
        r =>
          where(r.id === id)
            set (r.job := entity.job,
              r.clockedIn := entity.clockedIn,
              r.clockedOut := entity.clockedOut,
              r.duration := entity.duration,
              r.hourlyRate := entity.hourlyRate,
              r.earnings := entity.earnings,
              r.comment := entity.comment,
              r.tags := entity.tags,
              r.totalTimeAdjustment := entity.totalTimeAdjustment,
              r.totalEarningsAdjustment := entity.totalEarningsAdjustment))
      id
    }

    registration.id.map(id => updateEntity(id, registration.convert)) match {
      case None => Left("Could not update record")
      case Some(id) => Right(id)
    }
  }

  override def findById(id: Long): Option[Registration] = transaction {
    registrations.lookup(id).map(_.convert)
  }

  override def findBy(job: String, clockedIn: Option[LocalDateTime], clockedOut: Option[LocalDateTime]): Seq[Registration] =
    transaction {
      from(registrations)(
        registration =>
          where(
            registration.job === job
              and registration.clockedIn === clockedIn.map(Timestamp.valueOf)
              and registration.clockedOut === clockedOut.map(Timestamp.valueOf)) select registration).toList.map(_.convert)
    }
}
