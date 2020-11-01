package com.personal.hourstracker.service.presenter.config.component

import java.io.File

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.consolidated.presenter
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.service.presenter.Model

trait ConsolidatedRegistrationsHtmlPresenterComponent extends HtmlPresenterComponent {
  this: Configuration with LoggingComponent =>

  override val htmlPresenter: HtmlPresenter = new ConsolidatedRegistrationsHtmlPresenter()

  class ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter with Configuration {

    override val renderConsolidatedRegistrations: ConsolidatedRegistrations => String = consolidatedRegistrations => {
      logger.info(s"ConsolidatedRegistrationsHtmlPresenter should be writing to: ${Application.exportTo}")

      val model: Model = Model(
        consultantName = Application.consultantName,
        registrations = consolidatedRegistrations)

      logger.debug(s"ConsolidatedRegistrationsHtmlPresenter:renderConsolidatedRegistrations - consolidatedRegistrations = $consolidatedRegistrations")

      presenter.html.index.render(model).toString()
    }

    override val renderRegistrationsPerJobs: ConsolidatedRegistrationsPerJob => Seq[File] = consolidatedRegistrationsPerJob => {
      consolidatedRegistrationsPerJob.map {
        case (job, consolidatedRegistrations) => renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
      }.toSeq
    }

    override def renderRegistrationsPerSingleJob(job: String, registrations: ConsolidatedRegistrations): File = {
      logger.info(s"Rendering #${registrations.size} consolidated registrations for job '$job' to HTML")

      val outputFileName: String = fileName(job, registrations, ".html")
      withWriterTo(outputFileName) { writer =>
        writer.write(renderConsolidatedRegistrations(registrations))
        writer.flush()
      }
      new File(outputFileName)
    }
  }

}
