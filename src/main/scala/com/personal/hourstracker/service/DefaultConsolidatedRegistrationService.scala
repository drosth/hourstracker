package com.personal.hourstracker.service

import java.time.format.DateTimeFormatter

import com.personal.hourstracker.config.component.ConsolidatedRegistrationService
import com.personal.hourstracker.domain.{ConsolidatedRegistration, Registration}
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.domain.Registration.Registrations


trait DefaultConsolidatedRegistrationService extends ConsolidatedRegistrationService {

  def consolidatedRegistrationService: ConsolidatedRegistrationService = new DefaultConsolidatedRegistrationService()


  class DefaultConsolidatedRegistrationService extends ConsolidatedRegistrationService {

    private lazy val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private lazy val asConsolidatedRegistration: Registration => ConsolidatedRegistration = registration =>
      ConsolidatedRegistration(
        registration.clockedIn.get.toLocalDate,
        registration.job,
        determineDurationOf(registration),
        registration.comment
      )

    override def consolidateRegistrations(registrations: Registrations): ConsolidatedRegistrations = {
      registrations
          .map(asConsolidatedRegistration)
          .groupBy(_.date)
          .mapValues(consolidateConsolidatedRegistrations)
          .values
          .toSeq
          .flatten
    }

    private def consolidateConsolidatedRegistrations(consolidatedRegistrations: ConsolidatedRegistrations): ConsolidatedRegistrations = {
      def add(x: Option[Double], y: Option[Double]): Option[Double] = x match {
        case None => y
        case Some(a) => y match {
          case None => x
          case Some(b) => Some(a + b)
        }
      }

      def aggregateComments(x: Option[String], y: Option[String]): Option[String] = x match {
        case None => y
        case Some(a) => y match {
          case None => x
          case Some(b) => Some(s"$a $b".trim)
        }
      }

      val aggregateConsolidatedRegistration: ConsolidatedRegistrations => ConsolidatedRegistration =
        registrations =>
          registrations
              .foldLeft(registrations.head.copy(duration = None, comment = None)) { (acc, registration) =>
                acc.copy(
                  duration = add(acc.duration, registration.duration),
                  comment = aggregateComments(acc.comment, registration.comment))
              }

      consolidatedRegistrations
          .groupBy(_.job)
          .mapValues(aggregateConsolidatedRegistration)
          .values
          .toSeq
    }

    private def determineDurationOf(registration: Registration): Option[Double] = registration.totalTimeAdjustment match {
      case None => registration.duration
      case Some(_) => registration.totalTimeAdjustment
    }
  }


}

