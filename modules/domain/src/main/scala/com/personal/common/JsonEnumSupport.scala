package com.personal.common

trait JsonEnumSupport {
  this: DefaultJsonProtocol =>

  def jsonEnum[T <: Enumeration](enumType: T): JsonFormat[T#Value] =
    new JsonFormat[T#Value] {
      def write(obj: T#Value): JsValue =
        JsString(obj.toString)

      def read(json: JsValue): T#Value = json match {
        case JsString(value) => enumType.withName(value)
        case JsNumber(id: BigDecimal) =>
          enumType.values
            .find(_.id == id.intValue)
            .getOrElse(throw DeserializationException(s"Expected a value from enum $enumType"))
        case _ =>
          throw DeserializationException(s"Expected a value from enum $enumType")
      }
    }
}
