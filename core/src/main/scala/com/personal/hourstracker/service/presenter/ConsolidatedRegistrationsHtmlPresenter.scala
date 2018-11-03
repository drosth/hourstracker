package com.personal.hourstracker.service.presenter

import java.io.File

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.presenter.html.ConsolidatedRegistrationsPresenter
import com.personal.hourstracker.service.presenter.config.HtmlPresenter

trait ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrationsPerJob] with Configuration {
  this: LoggingComponent =>

  val htmlPresenter: ConsolidatedRegistrationsHtmlPresenter = new ConsolidatedRegistrationsHtmlPresenter()

  class ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrationsPerJob] with Configuration {

    override val renderConsolidatedRegistrations: ConsolidatedRegistrations => String = consolidatedRegistrations => {
      val model: Model = Model(
        consultantName = Application.consultantName,
        registrations = consolidatedRegistrations)

      ConsolidatedRegistrationsPresenter.render(model).toString()
    }

    override val renderRegistrationsPerJob: ConsolidatedRegistrationsPerJob => Seq[File] = consolidatedRegistrationsPerJob => {
      consolidatedRegistrationsPerJob.map {
        case (job, consolidatedRegistrations) =>
          logger.info(s"Rendering #${consolidatedRegistrations.size} consolidated registrations for job '$job' to HTML")

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
