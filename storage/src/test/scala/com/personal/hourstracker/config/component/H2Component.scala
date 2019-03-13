package com.personal.hourstracker.config.component

import scalikejdbc.ConnectionPool

trait H2Component extends DBComponent {

  // required to pre-load H2 driver
  private val _ = Class.forName("org.h2.Driver")

  ConnectionPool.singleton(
    "jdbc:h2:mem:hourstracker;MODE=MYSQL",
    "sa",
    "")
}
