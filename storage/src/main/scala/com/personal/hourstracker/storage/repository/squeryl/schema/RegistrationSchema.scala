package com.personal.hourstracker.storage.repository.squeryl.schema
import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import org.slf4j.{ Logger, LoggerFactory }
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{ Schema, Table }

object RegistrationSchema extends Schema {

  private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private def nameToTableMap(): Map[String, Table[_]] = {
    transaction {
      tables
        .map(table => {
          println(s"Table present: $table")
          table
        })
        .map(table => table.name -> table).toMap
    }
  }

  def initialize(): Unit =
    if (!nameToTableMap().contains("REGISTRATION")) {
      transaction {
        logger.info("Create Registration schema")
        RegistrationSchema.create
      }
    }

  implicit def localDateTimeToTimestamp(source: LocalDateTime): Timestamp =
    Timestamp.valueOf(source)

  val registrations: Table[RegistrationEntity] = table[RegistrationEntity]("REGISTRATION")

  on(registrations)(r => declare(r.job is indexed, columns(r.job, r.clockedIn, r.clockedOut) are (indexed, unique)))
}
