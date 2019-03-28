package com.personal.hourstracker.storage.repository.squeryl.schema
import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{ Schema, Table }

object RegistrationSchema extends Schema {

  def reset() = transaction {
    RegistrationSchema.drop
    RegistrationSchema.create
    println("Created the schema")
  }

  implicit def localDateTimeToTimestamp(source: LocalDateTime): Timestamp =
    Timestamp.valueOf(source)

  val registrations: Table[RegistrationEntity] = table[RegistrationEntity]("REGISTRATION")

  on(registrations)(r => declare(r.job is indexed, columns(r.job, r.clockedIn, r.clockedOut) are (indexed, unique)))
}
