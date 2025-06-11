package com.personal.hourstracker.storage.repository.squeryl.schema

import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import org.squeryl.{PrimitiveTypeMode, Schema, Table}

import java.sql.Timestamp
import java.time.LocalDateTime

private object InternalFieldMapper extends PrimitiveTypeMode
import com.personal.hourstracker.storage.repository.squeryl.schema.InternalFieldMapper._

object RegistrationSchema extends Schema with PrimitiveTypeMode {
//  private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

//  def initialize(): Unit = transaction {
//    logger.info("Initializing schema")
//    RegistrationSchema.create
//  }

  implicit def localDateTimeToTimestamp(source: LocalDateTime): Timestamp =
    Timestamp.valueOf(source)

  val registrations: Table[RegistrationEntity] = table[RegistrationEntity]("hours_registered")

  on(registrations)(r =>
    declare(
      r.job is indexed,
      columns(r.job, r.clockedIn, r.clockedOut) are (indexed, unique)
    )
  )
}
