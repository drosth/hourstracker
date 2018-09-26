package com.personal.hourstracker.service

import java.text.NumberFormat
import java.util.Locale

import com.personal.hourstracker.config.component.FacturationService
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations


trait DefaultFacturationService extends FacturationService {
  override def facturationService: FacturationService = new DefaultFacturationService()


  class DefaultFacturationService extends FacturationService {

    private lazy val format: NumberFormat = NumberFormat.getInstance(new Locale("nl", "NL"))
    override val splitForFacturation: Registration => Registrations =
      registration => splitOnTags(registration)
    private val tagPattern = raw"^([^-]+)-([0-9]{1,2})%$$|(.*)".r

    def splitForFacturation(registrations: Registrations): Registrations = {
      registrations
          .flatMap(splitForFacturation)
    }

    def constructJobWithTag(job: String, tag: String): String = {
      splitTag(tag) match {
        case None => job
        case Some((t, None)) => s"${job} - [$t]"
        case Some((t, Some(p))) => s"${job} - [$t] ($p)"
      }
    }

    private def splitTag(tag: String): Option[Tuple2[Option[String], Option[Double]]] = {

      tagPattern.findFirstMatchIn(tag) map { patternMatch =>
        Option(patternMatch.group(1)) match {
          case None => (Option(patternMatch.group(0)).map(_.trim), None)
          case Some(_) => (Option(patternMatch.group(1)).map(_.trim), Option(patternMatch.group(2)).map(p => toDouble(p)))
        }
      }
    }

    private def toDouble(value: String): Double = value match {
      case x if x.length == 0 => 0
      case x => format.parse(value).doubleValue()
    }

    private def splitOnTags(registration: Registration): Registrations = registration.tags match {
      case None => Seq(registration)
      case Some(tags) => tags match {
        case x: Set[String] if x.isEmpty => Seq(registration)
        case x: Set[String] if x.size == 1 => Seq(registration)
        case x: Set[String] =>
          x.map { tag =>
            registration.copy(
              job = s"${registration.job} - $tag",
              tags = Some(Set(tag)),
              totalTimeAdjustment = calculateTotalTimeAdjustmentUsing(tag, registration.duration)
            )
          }.toSeq
      }
    }

    private def calculateTotalTimeAdjustmentUsing(tag: String, duration: Option[Double]): Option[Double] = duration match {
      case None => None
      case Some(d) =>
        splitTag(tag) match {
          case None => None
          case Some((_, None)) => duration
          case Some((_, Some(percentage))) =>
            Some(BigDecimal(d * (percentage / 100)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)
        }
    }
  }


}
