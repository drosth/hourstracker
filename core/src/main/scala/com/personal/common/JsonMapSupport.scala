package com.personal.common
import spray.json.{ deserializationError, serializationError, JsArray, JsFalse, JsNumber, JsObject, JsonFormat, JsString, JsTrue, JsValue }

trait JsonMapSupport {
  implicit object AnyJsonFormat extends JsonFormat[Any] {
    def write(x: Any): JsValue = x match {
      case number: Int => JsNumber(number)
      case string: String => JsString(string)
      case boolean: Boolean if boolean => JsTrue
      case boolean: Boolean if !boolean => JsFalse
      case map: Map[String, _] =>
        JsObject(map.map {
          case (key, value) => key -> write(value)
        })
      case vector: Vector[Any] => JsArray(vector.map(write))
      case list: List[Any] => JsArray(list.toVector.map(write))
      case unSupported =>
        serializationError(s"Serialization of this type is not supported: ${unSupported.toString}")
    }

    def read(value: JsValue): Any = value match {
      case JsObject(fields) => fields.map(entry => entry._1 -> read(entry._2))
      case JsArray(elements) => elements.map(read)
      case JsNumber(number) => number.intValue()
      case JsString(string) => string
      case JsTrue => true
      case JsFalse => false
      case unSupported =>
        deserializationError(s"Deserialization of this type is not supported: ${unSupported.toString}")
    }
  }
}
