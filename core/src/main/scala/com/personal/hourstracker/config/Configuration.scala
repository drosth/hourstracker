package com.personal.hourstracker.config

import com.typesafe.config.ConfigFactory

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

  object Postgres {
    lazy val url: String = config.getString(s"$Namespace.database.postgres.url")
    lazy val driver: String = config.getString(s"$Namespace.database.postgres.driver")
    lazy val connectionPool: Boolean = config.getBoolean(s"$Namespace.database.postgres.connectionPool")
    lazy val keepAliveConnection: Boolean = config.getBoolean(s"$Namespace.database.postgres.keepAliveConnection")
  }

  object H2 {
    lazy val url: String = config.getString(s"$Namespace.database.h2.url")
    lazy val user: String = config.getString(s"$Namespace.database.h2.user")
    lazy val password: String = config.getString(s"$Namespace.database.h2.password")
    lazy val driver: String = config.getString(s"$Namespace.database.h2.driver")
    lazy val connectionPool: Boolean = config.getBoolean(s"$Namespace.database.h2.connectionPool")
    lazy val keepAliveConnection: Boolean = config.getBoolean(s"$Namespace.database.h2.keepAliveConnection")
  }

  //  object ConsolidatedRegistrations {
  //    lazy val host: String = config.getString(s"$Namespace.http.bind.host")
  //    lazy val port: Int = config.getInt(s"$Namespace.http.bind.port")
  //  }
}
