package com.personal.hourstracker.storage.repository.squeryl.entities
import java.sql.Timestamp

import org.squeryl.annotations.Column

case class RegistrationEntity(
  var job: String,
  var clockedIn: Option[Timestamp] = None,
  var clockedOut: Option[Timestamp] = None,
  @Column(scale = 2) duration: Option[Double] = None,
  var hourlyRate: Option[Double] = None,
  var earnings: Option[Double] = None,
  var comment: Option[String] = None,
  var tags: Option[String] = None,
  var totalTimeAdjustment: Option[Double] = None,
  var totalEarningsAdjustment: Option[Double] = None) extends BaseEntity {

  /*
    Important:
      If a class has an Option[] field, it becomes mandatory to implement a zero argument constructor that
      initializes Option[] fields with Some() instances

      Failing to do so will cause an exception to be thrown when the table will be instantiated
   */
  def this() =
    this(
      job = "undefined",
      clockedIn = Some(new Timestamp(System.currentTimeMillis)),
      clockedOut = Some(new Timestamp(System.currentTimeMillis)),
      duration = Some(0.toDouble),
      hourlyRate = Some(0.toDouble),
      earnings = Some(0.toDouble),
      comment = Some("undefined"),
      tags = Some(""),
      totalTimeAdjustment = Some(0.toDouble),
      totalEarningsAdjustment = Some(0.toDouble))
}
