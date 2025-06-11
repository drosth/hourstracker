package com.personal.hourstracker.stepdefinitions

import io.cucumber.datatable.DataTable

import java.time.{LocalDateTime, ZoneId}
import java.util.Date
import scala.collection.JavaConverters._

object Helpers {
  implicit def optionToOption[A](source: Option[String]): Option[A] = source.map(_.asInstanceOf[A])

  implicit def toLocalDateTime(source: Date): LocalDateTime = LocalDateTime.ofInstant(source.toInstant, ZoneId.systemDefault())

  implicit class DataTableToListOfMaps(dataTable: DataTable) {

    def withMaps[A](f: Map[String, String] => A): List[A] =
      dataTable
        .asMaps(classOf[String], classOf[String])
        .asScala
        .toList
        .map(row => row.asScala.toMap)
        .map(row => f(row))
  }
}
