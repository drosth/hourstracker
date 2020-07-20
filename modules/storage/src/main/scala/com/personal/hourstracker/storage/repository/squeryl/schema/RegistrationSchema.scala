package com.personal.hourstracker.storage.repository.squeryl.schema

import java.sql.Timestamp
import java.time.LocalDateTime

object RegistrationSchema extends Schema {

  private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def initialize(): Unit = transaction {
    logger.info("Initializing schema")
    RegistrationSchema.create
  }

  implicit def localDateTimeToTimestamp(source: LocalDateTime): Timestamp =
    Timestamp.valueOf(source)

  val registrations: Table[RegistrationEntity] = table[RegistrationEntity]("registration")

  on(registrations)(r => declare(r.job is indexed, columns(r.job, r.clockedIn, r.clockedOut) are(indexed, unique)))
}
