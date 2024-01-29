package com.personal.hourstracker.service.impl

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.concurrent.ExecutionContext

import com.personal.hourstracker.domain.{ ConsolidatedRegistration, Registration }
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob, DateTimeOrdering }
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.ConsolidatedRegistrationService
import org.slf4j.Logger

class DefaultConsolidatedRegistrationService(implicit logger: Logger, executionContext: ExecutionContext)
  extends ConsolidatedRegistrationService {

  lazy implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  private lazy val asConsolidatedRegistration: Registration => ConsolidatedRegistration = registration =>
    ConsolidatedRegistration(
      registration.clockedIn.get.toLocalDate,
      registration.job,
      determineDurationOf(registration),
      registration.comment)

  override def consolidateAndProcessRegistrations[T](registrations: Registrations)(processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => T): T = {

    val consolidatedRegistrationsPerJob = addUnregisteredEntriesTo(
      consolidateRegistrations(registrations).groupBy(_.job))

    processConsolidatedRegistrations(consolidatedRegistrationsPerJob)
  }

  private def consolidateRegistrations(registrations: Registrations): ConsolidatedRegistrations = {
    registrations
      .map(asConsolidatedRegistration)
      .groupBy(_.date)
      .mapValues(consolidateConsolidatedRegistrations)
      .values
      .toList
      .flatten
  }

  private def consolidateConsolidatedRegistrations(consolidatedRegistrations: ConsolidatedRegistrations): ConsolidatedRegistrations = {
    def add(x: Option[Double], y: Option[Double]): Option[Double] = x match {
      case None => y
      case Some(a) =>
        y match {
          case None => x
          case Some(b) => Some(a + b)
        }
    }

    def aggregateComments(x: Option[String], y: Option[String]): Option[String] = x match {
      case None => y
      case Some(a) =>
        y match {
          case None => x
          case Some(b) => Some(s"$a $b".trim)
        }
    }

    val aggregateConsolidatedRegistration: ConsolidatedRegistrations => ConsolidatedRegistration =
      registrations =>
        registrations
          .foldLeft(registrations.head.copy(duration = None, comment = None)) { (acc, registration) =>
            acc.copy(duration = add(acc.duration, registration.duration), comment = aggregateComments(acc.comment, registration.comment))
          }

    consolidatedRegistrations
      .groupBy(_.job)
      .mapValues(aggregateConsolidatedRegistration)
      .values
      .toList
  }

  private def addUnregisteredEntriesTo(consolidatedRegistrationsPerJob: ConsolidatedRegistrationsPerJob): ConsolidatedRegistrationsPerJob = {

    consolidatedRegistrationsPerJob
      .map {
        case (job, registrations) =>
          val sorted = registrations.sorted(DateTimeOrdering)

          val firstDate: LocalDate = sorted.head.date.withDayOfMonth(1)
          val lastDate = sorted.last.date.withDayOfMonth(1).plusMonths(1)

          val actualDates = registrations.map(_.date)

          val expectedDates: List[LocalDate] = Iterator.iterate(firstDate) { _.plusDays(1) }.takeWhile(_.isBefore(lastDate)).toList

          val datesToAdd: List[LocalDate] = expectedDates.filterNot(actualDates.toSet)

          val addedRegistrations: List[ConsolidatedRegistration] = datesToAdd.foldLeft(registrations) { (acc, dateToAdd) =>
            val newConsolidatedRegistration: ConsolidatedRegistration =
              ConsolidatedRegistration(dateToAdd, "", None, None)
            acc :+ newConsolidatedRegistration
          }
          (job, addedRegistrations.sorted(DateTimeOrdering))
      }
  }

  private def determineDurationOf(registration: Registration): Option[Double] =
    registration.totalTimeAdjustment match {
      case None => registration.duration
      case Some(_) => registration.totalTimeAdjustment
    }
}
