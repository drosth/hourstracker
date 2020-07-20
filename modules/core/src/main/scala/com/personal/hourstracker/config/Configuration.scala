package com.personal.hourstracker.config

private[config] case class Database(
                                     connectionPool: DatabaseConnectionPool,
                                     connectionString: String,
                                     connectionTimeout: Long,
                                     driver: String,
                                     password: String,
                                     user: String,
                                     validationQuery: String
                                   )

private[config] case class DatabaseConnectionPool(initialConnections: Int = 0, maxConnections: Int)

trait Configuration {
  final val Namespace = "com.personal.hourstracker"

  def config = ConfigFactory.load()

  implicit lazy val ApplicationEnvironment: Environment = Application.environment

  object Application {
    lazy val environment: Environment = Environment(config.getString(s"$Namespace.env"))
    lazy val importFrom: String = config.getString(s"$Namespace.application.importFrom")
    lazy val consultantName: String = config.getString(s"$Namespace.application.consultantName")
    lazy val exportTo: String = config.getString(s"$Namespace.application.exportTo")
  }

  object Server {
    lazy val host: String = config.getString(s"$Namespace.rest.http.bind.host")
    lazy val port: Int = config.getInt(s"$Namespace.rest.http.bind.port")

    object Api {
      lazy val basePath: String = config.getString(s"$Namespace.rest.http.api.basePath")
    }

  }

}
