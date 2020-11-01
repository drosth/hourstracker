package com.personal.hourstracker.service.presenter.config.component

import java.io.File

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.service.presenter.{ Model, Presenter }
import spray.json._

trait JsonPresenterComponent {

  def jsonPresenter: JsonPresenter
}

trait JsonPresenter extends Presenter {
  this: Configuration =>
}

trait DefaultJsonPresenterComponent extends JsonPresenterComponent {
  this: LoggingComponent =>

  val jsonPresenter: JsonPresenter = new DefaultJsonPresenter()

  class DefaultJsonPresenter extends JsonPresenter with Configuration with Model.Protocol {

    override val renderConsolidatedRegistrations: ConsolidatedRegistrations => String = consolidatedRegistrations => {
      logger.info(s"ConsolidatedRegistrationsHtmlPresenter should be writing to: ${Application.exportTo}")

      val model: Model = Model(
        consultantName = Application.consultantName,
        registrations = consolidatedRegistrations)

      model.toJson.toString
    }

    override def renderRegistrationsPerSingleJob(job: String, registrations: ConsolidatedRegistrations): File = {
      logger.info(s"Rendering #${registrations.size} consolidated registrations for job '$job' to JSON")

      val outputFileName: String = fileName(job, registrations, ".json")
      withWriterTo(outputFileName) { writer =>
        writer.write(renderConsolidatedRegistrations(registrations))
        writer.flush()
      }
      new File(outputFileName)
    }
  }

}
