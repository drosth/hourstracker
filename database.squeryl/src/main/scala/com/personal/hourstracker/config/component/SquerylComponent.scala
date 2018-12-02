package com.personal.hourstracker.config.component

import com.personal.hourstracker.config.Configuration
import com.typesafe.config.{ Config, ConfigFactory }
import org.squeryl.{ Session, SessionFactory }
import org.squeryl.adapters.{ H2Adapter, MySQLAdapter }

trait SquerylComponent {
  def databaseSession: DatabaseSession
}

trait DatabaseSession {
  def start(): Unit
}

trait SquerylComponentForH2 extends SquerylComponent {
  this: Configuration =>

  val withConfig: Config = {
    ConfigFactory.load().getConfig(Namespace).getConfig("database.h2")
  }

  def databaseSession: DatabaseSession = new H2DatabaseSession(withConfig)

  class H2DatabaseSession(config: Config) extends DatabaseSession {

    override def start(): Unit = {
      println(s"""config.getString("url"): ${config.getString("url")}""")
      Class.forName(config.getString("driver"))
      SessionFactory.concreteFactory = Some(
        () =>
          Session.create(
            java.sql.DriverManager.getConnection(
              config.getString("url"),
              config.getString("user"),
              config.getString("password")),
            new H2Adapter))
    }
  }
}

trait SquerylComponentForMySQL extends SquerylComponent {
  this: Configuration =>

  val withConfig: Config = {
    ConfigFactory.load().getConfig(Namespace).getConfig("database.mysql")
  }

  def databaseSession: DatabaseSession = new MySQLDatabaseSession(withConfig)

  class MySQLDatabaseSession(config: Config) extends DatabaseSession {
    override def start(): Unit = {
      Class.forName(config.getString("driver"))
      SessionFactory.concreteFactory = Some(
        () =>
          Session.create(
            java.sql.DriverManager.getConnection(
              config.getString("url"),
              config.getString("user"),
              config.getString("password")),
            new MySQLAdapter))
    }
  }
}
