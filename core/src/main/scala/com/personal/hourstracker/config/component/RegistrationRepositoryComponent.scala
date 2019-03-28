package com.personal.hourstracker.config.component
import com.personal.hourstracker.storage.repository.RegistrationRepository

trait RegistrationRepositoryComponent {
  def registrationRepository: RegistrationRepository
}
