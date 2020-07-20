package com.personal.hourstracker.storage.config

trait StorageConfiguration extends Configuration {

  object Storage {
    object Registrations {
      lazy val user: String = config.getString(s"$Namespace.storage.registrations.user.name")
      lazy val password: String = config.getString(s"$Namespace.storage.registrations.user.password")
      lazy val url: String = config.getString(s"$Namespace.storage.registrations.db.connectionString")
      lazy val driver: String = config.getString(s"$Namespace.storage.registrations.db.driver")

      object ConnectionPool {
        lazy val initialSize: Int = config.getInt(s"$Namespace.storage.registrations.db.connectionpool.initialSize")
      }
    }
  }
}
