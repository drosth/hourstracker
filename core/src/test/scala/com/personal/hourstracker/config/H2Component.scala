package com.personal.hourstracker.config

trait H2Component extends DBComponent {

  // required to pre-load H2 driver
  private val _ = Class.forName("org.h2.Driver")

  ConnectionPool.add(
    'Projection,
    "jdbc:h2:mem:jobprojection;MODE=MYSQL",
    "sa",
    ""
  )
}
