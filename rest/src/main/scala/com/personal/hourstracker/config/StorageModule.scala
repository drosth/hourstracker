package com.personal.hourstracker.config

import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.config.component.SquerylRegistrationRepositoryComponent

trait StorageModule extends SquerylRegistrationRepositoryComponent with StorageConfiguration {

}
