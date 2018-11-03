package com.personal.hourstracker.service.presenter.impl

import java.io.File

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.ConsolidatedRegistration
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.presenter.html.ConsolidatedRegistrationsPresenter
import com.personal.hourstracker.service.presenter.{ HtmlPresenter, Model }

trait ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrationsPerJob] with Configuration {
  this: LoggingComponent =>

  val htmlPresenter: ConsolidatedRegistrationsHtmlPresenter = new ConsolidatedRegistrationsHtmlPresenter()

  class ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrationsPerJob] with Configuration {

    override val renderConsolidatedRegistrations: ConsolidatedRegistrations => String = consolidatedRegistrations => {
      val model: Model = Model(
        consultantName = Application.consultantName,
        registrations = consolidatedRegistrations,
        totalHours = calculateTotalDuration(consolidatedRegistrations),
        monthName = fullMonthNameOf(consolidatedRegistrations.head))

      ConsolidatedRegistrationsPresenter.render(model).toString()
    }

    val fullMonthNameOf: ConsolidatedRegistration => String = consolidatedRegistration =>
      consolidatedRegistration.date.format(monthFormatter)

    val calculateTotalDuration: ConsolidatedRegistrations => Double = registrations =>
      registrations
        .filter(_.duration.isDefined)
        .foldLeft(0d)((a, i) => a + i.duration.get)

    override def renderRegistrationsPerJob: ConsolidatedRegistrationsPerJob => Seq[File] = consolidatedRegistrationsPerJob => {
      consolidatedRegistrationsPerJob.map {
        case (job, consolidatedRegistrations) =>
          logger.info(s"Rendering #${consolidatedRegistrations.size} consolidated registrations to HTML")

          val outputFileName: String = fileName(job, consolidatedRegistrations, ".html")
          withWriterTo(outputFileName) { writer =>
            writer.write(renderConsolidatedRegistrations(consolidatedRegistrations))
            writer.flush()
          }
          new File(outputFileName)
      }.toSeq
    }
  }

}
