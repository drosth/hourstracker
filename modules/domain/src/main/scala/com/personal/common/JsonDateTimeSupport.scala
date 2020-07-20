package com.personal.common
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

trait JsonDateTimeSupport {
  this: DefaultJsonProtocol =>

  def jsonDate(): JsonFormat[LocalDate] = new JsonFormat[LocalDate] {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    def write(dateTime: LocalDate) = JsString(dateTime.format(formatter))

    def read(json: JsValue): LocalDate = json match {
      case JsString(a) => LocalDate.parse(a, formatter)
      case _ => deserializationError("String expected")
    }
  }

  def jsonDateTime(): JsonFormat[LocalDateTime] =
    new JsonFormat[LocalDateTime] {
      private val formatter: DateTimeFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME

      def write(dateTime: LocalDateTime) = JsString(dateTime.format(formatter))

      def read(json: JsValue): LocalDateTime = json match {
        case JsString(a) => LocalDateTime.parse(a, formatter)
        case _ => deserializationError("String expected")
      }
    }

  implicit lazy val dateTimeFormat: JsonFormat[LocalDateTime] = jsonDateTime()
  implicit lazy val dateFormat: JsonFormat[LocalDate] = jsonDate()
}
