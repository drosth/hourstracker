package com.personal.hourstracker

import java.io.{File, FileInputStream, InputStreamReader, Reader}
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


package object repository {

  lazy val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
  lazy val format: NumberFormat = NumberFormat.getInstance(new Locale("nl", "NL"))
  lazy val DEFAULT_ENCODING = "UTF-8"

  implicit def toLocalDateTime(value: Option[String]): Option[LocalDateTime] = value match {
    case Some(v) => Some(LocalDateTime.parse(v, dateTimeFormatter))
    case None => None
  }

  implicit def toDouble(value: String): Double = value match {
    case x if x.length == 0 => 0
    case x => format.parse(value).doubleValue()
  }

  implicit def toDouble(value: Option[String]): Option[Double] = value match {
    case Some(v) => Some(toDouble(v))
    case None => None
  }

  implicit def toReader(fileName: String): Reader = {
    new File(fileName)
  }

  implicit def toReader(file: File): Reader = {
    new InputStreamReader(new FileInputStream(file), DEFAULT_ENCODING)
  }
}
