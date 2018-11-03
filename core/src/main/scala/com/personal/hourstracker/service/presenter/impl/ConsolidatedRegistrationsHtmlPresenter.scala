package com.personal.hourstracker.service.presenter.impl

import java.io.File

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.presenter.html.ConsolidatedRegistrationsPresenter
import com.personal.hourstracker.service.presenter.{ BackOffice, HtmlPresenter, Model }

trait ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrationsPerJob] {
  this: LoggingComponent =>

  val htmlPresenter: ConsolidatedRegistrationsHtmlPresenter = new ConsolidatedRegistrationsHtmlPresenter()

  class ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrationsPerJob] {

    /*
    override def renderRegistrationsTo(registrations: ConsolidatedRegistrationsPerJob, fileName: String): File = {
      logger.info(s"Rendering #${registrations.size} consolidated registrations to HTML: '$fileName'")

      withWriterTo(fileName) { writer =>
        writer.write(renderRegistrations(registrations))
        writer.flush()
      }
      new File(fileName)
    }
    */

    val fullMonthNameOf: ConsolidatedRegistration => String = consolidatedRegistration =>
      consolidatedRegistration.date.format(monthFormatter)

    val calculateTotalDuration: ConsolidatedRegistrations => Double = registrations =>
      registrations
        .filter(_.duration.isDefined)
        .foldLeft(0d)((a, i) => a + i.duration.get)

    override def renderConsolidatedRegistrations(consolidatedRegistrations: ConsolidatedRegistrations): String = {
      val model: Model = Model(
        consultantName = "Hans Drost",
        registrations = consolidatedRegistrations,
        totalHours = calculateTotalDuration(consolidatedRegistrations),
        monthName = fullMonthNameOf(consolidatedRegistrations.head),
        backoffice = BackOffice("backoffice@the-future-group.com"))

      ConsolidatedRegistrationsPresenter.render(model).toString()
    }

    override def renderRegistrations: ConsolidatedRegistrationsPerJob => Seq[File] = ???
  }
}
