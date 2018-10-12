package com.personal.hourstracker.config

import com.typesafe.config.ConfigFactory

trait Configuration {
  final private val Namespace = "com.personal.hourstracker"
  private val config = ConfigFactory.load()

  implicit lazy val ApplicationEnvironment: Environment =
    Application.environment

  object Application {
    lazy val environment: Environment = Environment(config.getString(s"$Namespace.env"))
    lazy val importFrom: String = config.getString(s"$Namespace.application.importFrom")
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
}
