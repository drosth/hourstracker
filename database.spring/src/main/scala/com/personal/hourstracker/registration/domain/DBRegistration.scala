package com.personal.hourstracker.registration.domain

import java.sql.Timestamp

import javax.persistence.{ Column, Entity, GeneratedValue, GenerationType, Id, Table }

@Entity
@Table(name = "REGISTRATIONS")
class DBRegistration(id: Option[Long] = None, job: String, clockedIn: Option[Timestamp] = None, clockedOut: Option[Timestamp] = None) extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID")
  var _id: Long = id.getOrElse(-1L)

  @Column(name = "JOB")
  var _job: String = job

  @Column(name = "CLOCKED_IN")
  var _clockedIn: Option[Timestamp] = None

  @Column(name = "CLOCKED_OUT")
  var _clockedOut: Option[Timestamp] = None

  def this() = this(None, "", None, None)

  //  clockedIn: Option[Timestamp] = None,
  //  clockedOut: Option[Timestamp] = None,
  //  duration: Option[Double] = None,
  //  hourlyRate: Option[Double] = None,
  //  earnings: Option[Double] = None,
  //  comment: Option[String] = None,
  //  //                         tags: Option[Set[String]] = None,
  //  breaks: Option[String] = None,
  //  adjustments: Option[String] = None,
  //  totalTimeAdjustment: Option[Double] = None,
  //  totalEarningsAdjustment: Option[String] = None) extends HourstrackerDBObject
}
