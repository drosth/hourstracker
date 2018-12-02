package com.personal.hourstracker

import com.personal.hourstracker.config.component.SquerylComponentForMySQL
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.DBRegistration
import com.personal.hourstracker.repository.HourstrackerDB

object Dummy extends App with SquerylComponentForMySQL with Configuration {

  private lazy val schema: HourstrackerDB = HourstrackerDB()
  import schema._

  databaseSession.start()

  transaction {
    schema.drop
    schema.create
    println("Created the schema:")
  }

  transaction {
    val registration = DBRegistration(job = "test")
    registrations.insert(registration)
    println(s"Inserted: $registration")
  }

  transaction {
    val queried: Iterable[DBRegistration] = registrations.allRows
    queried.foreach(row =>
      println(s"${row.id} => $row"))
  }
}
