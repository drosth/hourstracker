package com.personal.hourstracker.storage.repository

import com.personal.hourstracker.domain.Registration.{ RegistrationID, Registrations }
import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import scalikejdbc.{ DBSession, _ }

import scala.concurrent.{ ExecutionContext, Future }

class ScalikeJdbcMySQLRegistrationRepository(
  implicit
  session: DBSession,
  executionContext: ExecutionContext) extends RegistrationRepository {

  import com.personal.hourstracker.storage.repository.RegistrationConverters._

  initialize()

  def initialize(): Unit = createTable()

  private def createTable(): Unit = {
    sql"""
      CREATE TABLE IF NOT EXISTS Registration (
        id                        BIGINT AUTO_INCREMENT,
        job                       VARCHAR(255) NOT NULL,
        clocked_in                DATETIME,
        clocked_out               DATETIME,
        duration                  DECIMAL(5,2),
        hourly_rate               DECIMAL(5,2),
        earnings                  DECIMAL(5,2),
        comment                   VARCHAR(255),
        tags                      VARCHAR(255),
        total_time_adjustment     DECIMAL(5,2),
        total_earnings_adjustment DECIMAL(5,2),

        PRIMARY KEY (id)
      );
    """.execute().apply()
  }

  override def load()(implicit searchParameters: SearchParameters): Future[Registrations] = ???

  override def findById(id: Registration.RegistrationID): Future[Option[Registration]] = {
    Future({
      sql"""
       SELECT *
       FROM Registration
       WHERE id = $id
      """
        .map(_.convert())
        .single
        .apply()
    })
  }

  override def findAll(): Future[Either[String, Registrations]] = ???

  override def save(registration: Registration): Future[Either[String, RegistrationID]] = registration.id match {
    case Some(id) =>
      findById(id) flatMap {
        case None =>
          insert(registration)
            .map(registrationID => Right(registrationID))

        case Some(r) =>
          update(registration)
            .map(_ => Right(registration.id.get))
      }

    case None =>
      insert(registration)
        .map(registrationID => Right(registrationID))
  }

  private def insert(registration: Registration): Future[RegistrationID] =
    Future({
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
     """.updateAndReturnGeneratedKey("id").apply()
    })

  private def update(registration: Registration): Future[Unit] =
    Future({
      sql"""
        UPDATE Registration
        SET
           job                       = ${registration.job},
           clocked_in                = ${registration.clockedIn},
           clocked_out               = ${registration.clockedOut},
           duration                  = ${registration.duration},
           hourly_rate               = ${registration.hourlyRate},
           earnings                  = ${registration.earnings},
           comment                   = ${registration.comment},
           tags                      = ${registration.tags.map(_.mkString(";"))},
           total_time_adjustment     = ${registration.totalTimeAdjustment},
           total_earnings_adjustment = ${registration.totalEarningsAdjustment}
        WHERE id = ${registration.id}
      """.update.apply()
    })
}

object ScalikeJdbcMySQLRegistrationRepository {

  def apply(
    implicit
    session: DBSession,
    executionContext: ExecutionContext): RegistrationRepository = new ScalikeJdbcMySQLRegistrationRepository
}
