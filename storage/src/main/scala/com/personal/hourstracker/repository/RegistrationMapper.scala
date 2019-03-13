package com.personal.hourstracker.repository

import com.personal.hourstracker.domain.Registration
import scalikejdbc.WrappedResultSet

object RegistrationMapper {

  def toRegistration(rs: WrappedResultSet): Registration = {
    Registration(
      id = rs.longOpt("id"),
      job = rs.string("job"),
      clockedIn = rs.dateTimeOpt("clocked_in").map(value => value.toLocalDateTime),
      clockedOut = rs.dateTimeOpt("clocked_out").map(value => value.toLocalDateTime),
      duration = rs.doubleOpt("duration"),
      hourlyRate = rs.doubleOpt("hourly_rate"),
      earnings = rs.doubleOpt("earnings"),
      comment = rs.stringOpt("comment"),
      tags = rs.stringOpt("tags").map(_.split(";").toSet),
      totalTimeAdjustment = rs.doubleOpt("total_time_adjustment"),
      totalEarningsAdjustment = rs.doubleOpt("total_earnings_adjustment"))
  }

}
