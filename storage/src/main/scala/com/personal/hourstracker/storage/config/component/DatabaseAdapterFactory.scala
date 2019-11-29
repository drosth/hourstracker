package com.personal.hourstracker.storage.config.component

import org.slf4j.{ Logger, LoggerFactory }
import org.squeryl.adapters.{ H2Adapter, MySQLAdapter, PostgreSqlAdapter }
import org.squeryl.internals.DatabaseAdapter

object DatabaseAdapterFactory {
  private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def retrieveDatabaseAdapterFor(driver: String): Option[DatabaseAdapter] = {
    driver match {
      case "org.postgresql.Driver" =>
        logger.debug(s"Returning 'PostgreSqlAdapter' for driver '$driver'")
        Some(new PostgreSqlAdapter)
      case "org.h2.Driver" =>
        logger.debug(s"Returning 'H2Adapter' for driver '$driver'")
        Some(new H2Adapter)
      case "com.mysql.cj.jdbc.Driver" =>
        logger.debug(s"Returning 'MySQLAdapter' for driver '$driver'")
        Some(new MySQLAdapter)
      case _ =>
        logger.error(s"==== Could not create DatabaseAdapter for driver: $driver")
        None
    }
  }
}
