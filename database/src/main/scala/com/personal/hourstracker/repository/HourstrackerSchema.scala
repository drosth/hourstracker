package com.personal.hourstracker.repository

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.SquerylComponent
import com.personal.hourstracker.domain.DBRegistration
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, Session}

object HourstrackerSchema extends Schema {
  this: SquerylComponent with Configuration =>

  val registrations = table[DBRegistration]("REGISTRATIONS")

  on(registrations)(
    registration =>
      declare(
        registration.id is (autoIncremented("registrations_id_seq")),
        registration.job is (dbType("varchar(255)")),
        registration.comment is (dbType("varchar(255)"))
    )
  )

  printDdl(ddl => println(s"DDL: $ddl"))

  override def drop = {
    Session.cleanupResources
    super.drop
  }
}
