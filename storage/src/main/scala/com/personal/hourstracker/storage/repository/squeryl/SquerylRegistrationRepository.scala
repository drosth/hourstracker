package com.personal.hourstracker.storage.repository.squeryl

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.storage.repository.RegistrationRepository
import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity
import com.personal.hourstracker.storage.repository.squeryl.schema.RegistrationSchema
import org.squeryl.PrimitiveTypeMode.{ transaction, _ }

class SquerylRegistrationRepository extends RegistrationRepository {
  import RegistrationSchema._
  import com.personal.hourstracker.storage.repository.squeryl.converter.RegistrationConverters._

  override def findAll(): List[Registration] = transaction {
    registrations.toList.map(_.convert())
  }

  override def save(entity: Registration): Long = transaction {
    val inserted = registrations.insert(entity.convert())
    println(s"Inserted registration #${inserted.id}")
    inserted.id
  }

  override def findById(id: Long): Option[Registration] = transaction {
    registrations.where(r => r.id === id).headOption.map(_.convert())
  }
}
