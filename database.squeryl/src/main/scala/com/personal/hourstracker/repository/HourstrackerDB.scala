package com.personal.hourstracker.repository

import com.personal.hourstracker.domain.DBRegistration
import org.squeryl.{ Schema, Session }
import org.squeryl.PrimitiveTypeMode._

object HourstrackerDB {
  def apply(): HourstrackerDB = new HourstrackerDB()
}

class HourstrackerDB extends Schema with MyCustomTypes {

  val registrations = table[DBRegistration]("REGISTRATIONS")

  on(registrations)(
    registration =>
      declare(
        registration.id is (autoIncremented("registrations_id_seq")),
        registration.job is (dbType("varchar(255)")),
        registration.comment is (dbType("varchar(255)"))))

  printDdl(ddl => println(s"DDL: $ddl"))

  override def drop = {
    Session.cleanupResources
    super.drop
  }
}
