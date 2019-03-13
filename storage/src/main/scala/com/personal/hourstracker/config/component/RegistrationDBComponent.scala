package com.personal.hourstracker.config.component

import com.personal.hourstracker.config.Configuration
import scalikejdbc.{ ConnectionPool, ConnectionPoolSettings }

trait RegistrationDBComponent extends DBComponent {
  this: Configuration =>

  private val _ = Class.forName(RegistrationStore.database.driver)

  private val settings = ConnectionPoolSettings(
    initialSize = RegistrationStore.database.connectionPool.initialConnections,
    maxSize = RegistrationStore.database.connectionPool.maxConnections,
    connectionTimeoutMillis = RegistrationStore.database.connectionTimeout,
    validationQuery = RegistrationStore.database.validationQuery)

  ConnectionPool.singleton(
    RegistrationStore.database.connectionString,
    RegistrationStore.database.user,
    RegistrationStore.database.password,
    settings)
}
