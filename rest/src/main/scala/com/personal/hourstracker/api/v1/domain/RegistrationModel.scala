package com.personal.hourstracker.api.v1.domain
import java.time.LocalDateTime

import io.swagger.v3.oas.annotations.media.Schema

object RegistrationModel {
  type RegistrationModels = Seq[RegistrationModel]
}

@Schema
final case class RegistrationModel(
  job: String,
  clockedIn: Option[LocalDateTime],
  clockedOut: Option[LocalDateTime],
  duration: Option[Double],
  hourlyRate: Option[Double],
  earnings: Option[Double],
  comment: Option[String],
  tags: Option[Set[String]],
  totalTimeAdjustment: Option[Double],
  totalEarningsAdjustment: Option[String])
