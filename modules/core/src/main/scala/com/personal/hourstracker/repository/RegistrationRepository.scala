package com.personal.hourstracker.repository

import java.time.LocalDateTime

trait RegistrationRepository extends BaseRepository[Registration, Long] {
  def findBy(job: String, clockedIn: Option[LocalDateTime], clockedOut: Option[LocalDateTime]): Seq[Registration]
}
