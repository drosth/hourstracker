package com.personal.hourstracker.service

import java.text.NumberFormat
import java.util.Locale

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import org.slf4j.Logger

class DefaultFacturationService()(implicit logger: Logger, locale: Locale) extends FacturationService {

  private lazy val format: NumberFormat = NumberFormat.getInstance(locale)

  override val splitRegistrationForFacturation: Registration => Registrations = {
    logger.info("Splitting for facturation")
    registration =>
      splitOnTags(registration)
  }

  private val tagPattern = raw"^([^-]+)-([0-9]{1,2})%$$|(.*)".r

  def splitForFacturation(registrations: Registrations): Registrations = {
    registrations
      .flatMap(splitRegistrationForFacturation)
  }

  def constructJobWithTag(job: String, tag: String): String = {
    splitTag(tag) match {
      case Some((Some(t), _)) =>
        s"${job} - $t"
      case _ => job
    }
  }

  private def splitOnTags(registration: Registration): Registrations =
    registration.tags match {
      case None => Seq(registration)
      case Some(tags) =>
        tags match {
          case x: Set[String] if x.isEmpty => Seq(registration)
          case x: Set[String] if x.size == 1 => Seq(registration)
          case x: Set[String] =>
            x.map { tag =>
              registration.copy(
                job = constructJobWithTag(registration.job, tag),
                tags = Some(Set(tag)),
                totalTimeAdjustment = calculateTotalTimeAdjustmentUsing(tag, registration.duration))
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
          Some(
            BigDecimal(d * (percentage / 100))
              .setScale(2, BigDecimal.RoundingMode.HALF_UP)
              .toDouble)
      }
  }

  private def splitTag(tag: String): Option[Tuple2[Option[String], Option[Double]]] = {

    tagPattern.findFirstMatchIn(tag) map { patternMatch =>
      Option(patternMatch.group(1)) match {
        case None => (Option(patternMatch.group(0)).map(_.trim), None)
        case Some(_) =>
          (Option(patternMatch.group(1)).map(_.trim), Option(patternMatch.group(2)).map(p => toDouble(p)))
      }
    }
  }

  private def toDouble(value: String): Double = value match {
    case x if x.length == 0 => 0
    case x => format.parse(value).doubleValue()
  }

}
