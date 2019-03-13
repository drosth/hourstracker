package com.personal.hourstracker.config

import com.typesafe.config.ConfigFactory

private[config] case class Database(
  connectionPool: DatabaseConnectionPool,
  connectionString: String,
  connectionTimeout: Long,
  driver: String,
  password: String,
  user: String,
  validationQuery: String)

private[config] case class DatabaseConnectionPool(initialConnections: Int = 0, maxConnections: Int)

trait Configuration {
  final val Namespace = "com.personal.hourstracker"
  final val config = ConfigFactory.load()

  implicit lazy val ApplicationEnvironment: Environment =
    Application.environment

  object Application {
    lazy val environment: Environment = Environment(config.getString(s"$Namespace.env"))
    lazy val importFrom: String = config.getString(s"$Namespace.application.importFrom")
    lazy val consultantName: String = config.getString(s"$Namespace.application.consultantName")
    lazy val exportTo: String = config.getString(s"$Namespace.application.exportTo")
  }

  object Api {
    lazy val basePath: String = config.getString(s"$Namespace.http.api.basePath")
    lazy val host: String = config.getString(s"$Namespace.http.api.host")
    lazy val port: Int = config.getInt(s"$Namespace.http.api.port")
  }

  object Server {
    lazy val host: String = config.getString(s"$Namespace.http.bind.host")
    lazy val port: Int = config.getInt(s"$Namespace.http.bind.port")
  }

  object MySQL {
    lazy val url: String = config.getString(s"$Namespace.database.mysql.url")
    lazy val user: String = config.getString(s"$Namespace.database.mysql.user")
    lazy val password: String = config.getString(s"$Namespace.database.mysql.password")
    lazy val driver: String = config.getString(s"$Namespace.database.mysql.driver")
    lazy val connectionPool: Boolean = config.getBoolean(s"$Namespace.database.mysql.connectionPool")
    lazy val keepAliveConnection: Boolean = config.getBoolean(s"$Namespace.database.mysql.keepAliveConnection")
  }

  object RegistrationStore {
    lazy val database = Database(
      connectionPool = DatabaseConnectionPool(
        initialConnections = config.getInt(s"$Namespace.registration-store.db.connectionPool.initialConnections"),
        maxConnections = config.getInt(s"$Namespace.registration-store.db.connectionPool.maxConnections")),
      connectionString = config.getString(s"$Namespace.registration-store.db.connectionString"),
      connectionTimeout = config.getLong(s"$Namespace.registration-store.db.connectionTimeout"),
      driver = "com.mysql.jdbc.Driver",
      password = config.getString(s"$Namespace.registration-store.user.password"),
      user = config.getString(s"$Namespace.registration-store.user.name"),
      validationQuery = "select 1 from dual")
  }

  //  object ConsolidatedRegistrations {
  //    lazy val host: String = config.getString(s"$Namespace.http.bind.host")
  //    lazy val port: Int = config.getInt(s"$Namespace.http.bind.port")
  //  }
}
