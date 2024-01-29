package com.personal.hourstracker.storage.repository.squeryl.converter
import java.sql.Timestamp

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.storage.repository.Converter
import com.personal.hourstracker.storage.repository.squeryl.entities.RegistrationEntity

object RegistrationConverter {

  implicit class toRegistrationConverter(source: RegistrationEntity) extends Converter[Registration] {

    override lazy val convert: Registration = Registration(
      id = Some(source.id),
      job = source.job,
      clockedIn = source.clockedIn.map(_.toLocalDateTime),
      clockedOut = source.clockedOut.map(_.toLocalDateTime),
      duration = source.duration,
      hourlyRate = source.hourlyRate,
      earnings = source.earnings,
      comment = source.comment,
      tags = source.tags.map(t => t.split(";").toSet),
      totalTimeAdjustment = source.totalTimeAdjustment,
      totalEarningsAdjustment = source.totalEarningsAdjustment)
  }

  implicit class toRegistrationEntityConverter(source: Registration) extends Converter[RegistrationEntity] {

    override lazy val convert: RegistrationEntity = RegistrationEntity(
      job = source.job,
      clockedIn = source.clockedIn.map(Timestamp.valueOf),
      clockedOut = source.clockedOut.map(Timestamp.valueOf),
      duration = source.duration,
      hourlyRate = source.hourlyRate,
      earnings = source.earnings,
      comment = source.comment,
      tags = source.tags.map(t => t.mkString(";")),
      totalTimeAdjustment = source.totalTimeAdjustment,
      totalEarningsAdjustment = source.totalEarningsAdjustment)
  }
}
