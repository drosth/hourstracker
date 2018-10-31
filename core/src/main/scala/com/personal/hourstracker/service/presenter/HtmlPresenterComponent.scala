package com.personal.hourstracker.service.presenter

import java.io.{ File, PrintWriter, Writer }
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.presenter.html.ConsolidatedRegistrationsPresenter

trait HtmlPresenterComponent extends ConsolidatedRegistrationsHtmlPresenter with LoggingComponent

trait HtmlPresenter[T] {
  val htmlPresenter: HtmlPresenter[T]

  trait HtmlPresenter[T] {
    def renderRegistrationsTo(registrations: T, fileName: String): File

    def renderRegistrations(registrations: T): String
  }

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

case class Model(registrations: ConsolidatedRegistrations, totalHours: Double, monthName: String)

trait ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrations] {
  this: LoggingComponent =>

  val htmlPresenter: HtmlPresenter[ConsolidatedRegistrations] =
    new ConsolidatedRegistrationsHtmlPresenter()

  class ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrations] {

    override def renderRegistrationsTo(registrations: ConsolidatedRegistrations, fileName: String): File = {
      logger.info(s"Rendering #${registrations.size} consolidated registrations to HTML: '$fileName'")

      withWriterTo(fileName) { writer =>
        writer.write(renderRegistrations(registrations))
        writer.flush()
      }
      new File(fileName)
    }

    override def renderRegistrations(registrations: ConsolidatedRegistrations): String = {
      val total: Double = registrations
        .filter(_.duration.isDefined)
        .foldLeft(0d)((a, i) => a + i.duration.get)

      val month = registrations.head.date.getMonth().name()

      val model: Model = Model(registrations, total, month)

      ConsolidatedRegistrationsPresenter.render(model).toString()
    }
  }
}

object PresenterHelper {

  val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d MMM", new Locale("nl", "NL"))

  def toHumanReadable(value: LocalDate): String = {
    value.format(formatter)
  }

  def toHumanReadableHours(value: Double): String = {
    val hours = value.intValue()
    val min = ((value - hours) * 60).intValue()
    f"$hours:$min%02.0f"
  }

  def toHumanReadableHours(value: Option[Double]): String = value match {
    case None => ""
    case Some(v) => toHumanReadableHours(v)
  }
}
