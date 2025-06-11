package com.personal.hourstracker.storage.repository.squeryl.entities

import java.sql.Timestamp
import org.squeryl.KeyedEntity

class BaseEntity extends KeyedEntity[Long] {
  override val id: Long = 0
  val lastModified      = new Timestamp(System.currentTimeMillis)
}
