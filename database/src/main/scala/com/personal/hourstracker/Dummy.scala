package com.personal.hourstracker

import com.personal.hourstracker.config.component.{MySQL_SquerylComponent, SquerylComponent}
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.DBRegistration
import com.personal.hourstracker.repository.HourstrackerSchema


object Dummy extends App with SquerylComponent with MySQL_SquerylComponent with Configuration {

  import com.personal.hourstracker.repository.MyCustomTypes._

  databaseSession.start()

  transaction {
    HourstrackerSchema.drop
    HourstrackerSchema.create
    println("Created the schema:")
  }

  transaction {
    val registration = DBRegistration(job="test")
    HourstrackerSchema.registrations.insert(registration)
    println(s"Inserted: $registration")
  }

  transaction {
    val queried: Iterable[DBRegistration] = HourstrackerSchema.registrations.allRows
    queried.foreach(row =>
        println(s"${row.id} => $row")
    )
  }
}
