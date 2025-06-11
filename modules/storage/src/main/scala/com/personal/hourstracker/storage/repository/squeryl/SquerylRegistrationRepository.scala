package com.personal.hourstracker.storage.repository.squeryl

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.RegistrationService.{RegistrationRequest, SelectByYear, SelectByYearAndMonth}
import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import com.personal.hourstracker.storage.repository.squeryl.schema.RegistrationSchema
import org.slf4j.{Logger, LoggerFactory}

import java.sql.Timestamp
import java.time.{Instant, LocalDateTime}

class SquerylRegistrationRepository extends RegistrationRepository {
  import RegistrationSchema._
  import com.personal.hourstracker.storage.repository.squeryl.converter.RegistrationConverter._

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[RegistrationRepository])

//  def initialize(): Unit = {
//    transaction {
//      tables
//        .find(_.name.toUpperCase == "hours_registered") match {
//          case None =>
////            RegistrationSchema.initialize()
//            RegistrationSchema.printDdl(message => logger.info(message))
//          case _ =>
//            logger.info("Table 'REGISTRATION' already exists")
//            RegistrationSchema.printDdl(message => logger.info(message))
//        }
//    }
//  }

  override def findAll(): Source[Registration, NotUsed] =
    Source.fromIterator { () =>
      val registrations = findAllRegistrations()
      registrations.iterator
    }

  override def findByRequest(request: RegistrationRequest): Source[Registration, NotUsed] = {
    val requestedRegistrations: Registrations = request match {
      case SelectByYear(year) =>
        selectRegistrationsByYear(year)
      case SelectByYearAndMonth(year, month) =>
        selectRegistrationsByYearAndMonth(year, month)
      case _ => List()
    }

    Source.fromIterator(() => requestedRegistrations.iterator)
  }

  private def selectRegistrationsByYear(year: Int): Registrations = transaction {
    val lower = LocalDateTime.of(year, 1, 1, 0, 0, 0)
    val upper = LocalDateTime.of(year, 1, 1, 0, 0, 0).plusYears(1).minusSeconds(1)

    val r = registrations
      .where(r =>
        ((r.duration.isNotNull) and (r.duration.get gt 0))
          and ((r.clockedIn.isNotNull) and ((r.clockedIn.get gte Timestamp.valueOf(lower)) and (r.clockedIn.get lte Timestamp.valueOf(upper))))
      )
      .toList
      .map(_.convert)
    r
  }

  private def selectRegistrationsByYearAndMonth(year: Int, month: Int): Registrations = transaction {
    val lower = LocalDateTime.of(year, month, 1, 0, 0, 0)
    val upper = LocalDateTime.of(year, month, 1, 0, 0, 0).plusMonths(1).minusSeconds(1)

    registrations
      .where(r =>
        ((r.duration.isNotNull) and (r.duration.get gt 0))
          and ((r.clockedIn.isNotNull) and ((r.clockedIn.get gte Timestamp.valueOf(lower)) and (r.clockedIn.get lte Timestamp.valueOf(upper))))
      )
      .toList
      .map(_.convert)
  }

  private def findAllRegistrations(): Registrations = transaction {
    from(registrations)(select(_)).toList.map(_.convert)
  }

  override def save(registration: Registration): Either[String, Registration] = transaction {
    findBy(registration.job, registration.clockedIn, registration.clockedOut).headOption match {
      case None =>
        Right(registrations.insert(registration.copy(id = None).convert).convert)
      case Some(found) =>
        updateRegistration(registration.copy(id = found.id))
    }
  }

  private def updateRegistration(registration: Registration): Either[String, Registration] = {
    def updateEntity(id: Long, entity: RegistrationEntity): RegistrationEntity = transaction {
      update(registrations)(r =>
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
          r.totalEarningsAdjustment := entity.totalEarningsAdjustment)
      )
      entity
    }

    registration.id.map(id => updateEntity(id, registration.convert)).map(_.convert) match {
      case None =>
        Left(s"Could not update registration #${registration.id}")
      case Some(registration) =>
        Right(registration)
    }
  }

  override def findById(id: Long): Option[Registration] = transaction {
    registrations.lookup(id).map(_.convert)
  }

  override def findBy(job: String, clockedIn: Option[LocalDateTime], clockedOut: Option[LocalDateTime]): Seq[Registration] =
    transaction {
      from(registrations)(registration =>
        where(
          registration.job === job
            and registration.clockedIn === clockedIn.map(Timestamp.valueOf)
            and registration.clockedOut === clockedOut.map(Timestamp.valueOf)
        ) select registration
      ).toList.map(_.convert)
    }
}
