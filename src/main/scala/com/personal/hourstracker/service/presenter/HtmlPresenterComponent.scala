package com.personal.hourstracker.service.presenter

import java.io.{File, PrintWriter, Writer}

import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.presenter.html.ConsolidatedRegistrationsPresenter


trait HtmlPresenterComponent
    extends ConsolidatedRegistrationsHtmlPresenter


trait HtmlPresenter[T] {
  val presenter: HtmlPresenter[T]


  trait HtmlPresenter[T] {
    def renderRegistrationsTo(registrations: T, fileName: String)
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


trait ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrations] {
  val presenter: HtmlPresenter[ConsolidatedRegistrations] = new ConsolidatedRegistrationsHtmlPresenter()


  class ConsolidatedRegistrationsHtmlPresenter extends HtmlPresenter[ConsolidatedRegistrations] {

    import PresenterHelper._


    override def renderRegistrationsTo(registrations: ConsolidatedRegistrations, fileName: String): Unit = {

      val total: Double = registrations
          .filter(_.duration.isDefined)
          .foldLeft(0d)((a, i) => a + i.duration.get)

      val month = registrations.head.date.getMonth().name()

      val rendered = ConsolidatedRegistrationsPresenter.render(
        registrations,
        toHumanReadableHours(Option(total)),
        month
      ).toString()

      withWriterTo(fileName) { writer =>
        writer.write(rendered)
        writer.flush()
      }
    }
  }
}


object PresenterHelper {
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
