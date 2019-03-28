package com.personal.hourstracker.storage.config
import com.typesafe.config.ConfigFactory

trait Configuration {
  final val Namespace = "com.personal.hourstracker.storage"
  final lazy val config = ConfigFactory.load()

  object Storage {
    object Registrations {
      lazy val user: String = config.getString(s"$Namespace.registrations.user.name")
      lazy val password: String = config.getString(s"$Namespace.registrations.user.password")
      lazy val url: String = config.getString(s"$Namespace.registrations.db.connectionString")
      lazy val driver: String = config.getString(s"$Namespace.registrations.db.driver")
    }
  }
}
