package com.personal.hourstracker.storage.repository

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.storage.domain.RegistrationModel
import scalikejdbc.WrappedResultSet

object RegistrationConverters {

  trait Converter[A] {
    def convert(): A
  }

  implicit class wrappedResultSetToRegistrationConverter(source: WrappedResultSet) extends Converter[Registration] {
    override def convert(): Registration = {
      Registration(
        id = source.longOpt("id"),
        job = source.string("job"),
        clockedIn = source.dateTimeOpt("clocked_in").map(value => value.toLocalDateTime),
        clockedOut = source.dateTimeOpt("clocked_out").map(value => value.toLocalDateTime),
        duration = source.doubleOpt("duration"),
        hourlyRate = source.doubleOpt("hourly_rate"),
        earnings = source.doubleOpt("earnings"),
        comment = source.stringOpt("comment"),
        tags = source.stringOpt("tags").map(_.split(";").toSet),
        totalTimeAdjustment = source.doubleOpt("total_time_adjustment"),
        totalEarningsAdjustment = source.doubleOpt("total_earnings_adjustment"))
    }
  }

  implicit class registrationModelToRegistrationConverter(source: RegistrationModel) extends Converter[Registration] {
    override def convert(): Registration = {
      Registration(
        id = Some(source.id),
        job = source.job,
        clockedIn = source.clockedIn.map(_.toLocalDateTime),
        clockedOut = source.clockedOut.map(_.toLocalDateTime),
        duration = source.duration,
        hourlyRate = source.hourlyRate,
        earnings = source.earnings,
        comment = source.comment,
        //      tags = rs.stringOpt("tags").map(_.split(";").toSet),
        totalTimeAdjustment = source.totalTimeAdjustment,
        totalEarningsAdjustment = source.totalEarningsAdjustment)
    }
  }
}
