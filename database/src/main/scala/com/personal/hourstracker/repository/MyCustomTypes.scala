package com.personal.hourstracker.repository

import org.squeryl.PrimitiveTypeMode
import org.squeryl.dsl.{DeOptionizer, NonPrimitiveJdbcMapper, TOptionString, TString, TypedExpressionFactory}


object MyCustomTypes extends PrimitiveTypeMode {

  implicit val seqOfStringToStringTEF = new NonPrimitiveJdbcMapper[String, Set[String], TString](stringTEF, this) {
    override def convertFromJdbc(value: String): Set[String] = value.split(",").toSet
    override def convertToJdbc(value: Set[String]): String = value.mkString(",")
  }

  implicit val optionSeqOfStringToStringTEF =
    new TypedExpressionFactory[Option[Set[String]], TOptionString]
    with DeOptionizer[String, Set[String], TString, Option[Set[String]], TOptionString] {

      val deOptionizer = seqOfStringToStringTEF
    }
}
