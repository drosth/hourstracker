package com.personal.hourstracker.storage.repository.squeryl.schema
import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import org.slf4j.{ Logger, LoggerFactory }
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{ Schema, Table }

object RegistrationSchema extends Schema {

  private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  /*
  private val retrieveAvailableTablesAsMap: Map[String, Table[_]] = {
    transaction {
      tables
        .map(table => {
          logger.info(s"Table present: $table")
          table
        })
        .map(table => table.name.toUpperCase -> table)
        .toMap
    }
  }
  */

  def initialize(): Unit = transaction {
    logger.info("Initializing schema")
    RegistrationSchema.create
  }

  //  def initialize(): Unit = {
  //    logger.info("Initializing schema")
  //
  //    transaction {
  //      tables.find(_.name.toLowerCase == "registration") match {
  //        case None =>
  //          logger.info("Table 'registration' not present in schema")
  //          RegistrationSchema.create
  //
  //        case Some(table) =>
  //          val fields = table.posoMetaData.fieldsMetaData
  //          logger.info(s"Table 'registration' IS present in schema with fields: ${fields.map(_.columnName).mkString(", ")}")
  //      }
  //    }
  //  }

  implicit def localDateTimeToTimestamp(source: LocalDateTime): Timestamp =
    Timestamp.valueOf(source)

  val registrations: Table[RegistrationEntity] = table[RegistrationEntity]("registration")

  on(registrations)(r => declare(r.job is indexed, columns(r.job, r.clockedIn, r.clockedOut) are (indexed, unique)))
}
