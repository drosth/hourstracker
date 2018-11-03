package com.personal.hourstracker.service.presenter.config

import java.io.{ File, PrintWriter, Writer }
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.service.presenter.{ ConsolidatedRegistrationsHtmlPresenter, Presenter }

trait HtmlPresenterComponent extends ConsolidatedRegistrationsHtmlPresenter with Configuration with LoggingComponent

trait HtmlPresenter[T] {
  val htmlPresenter: Presenter[T]

  trait HtmlPresenter[T] extends Presenter[T] {
    this: Configuration =>

    val renderConsolidatedRegistrations: ConsolidatedRegistrations => String
  }

  val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM", new Locale("nl", "NL"))

  protected[presenter] def withWriterTo(fileName: String)(fn: Writer => Unit) {
    val writer: Writer = new PrintWriter(new File(fileName))
    try {
      fn(writer)
    } catch {
      case e: Exception =>
    } finally {
      writer.close()
    }
  }
}
