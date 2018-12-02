package com.personal.hourstracker.registration.repository

import com.personal.hourstracker.registration.domain.DBRegistration
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
trait RegistrationRepository extends CrudRepository[DBRegistration, Long] {
}
