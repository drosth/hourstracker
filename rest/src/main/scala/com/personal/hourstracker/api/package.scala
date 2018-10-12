package com.personal.hourstracker

package object api {
  case class Version(major: Int, minor: Int = 0) {
    lazy val value: String = major + (minor match {
      case minor if minor > 0 => s".${minor.toString}"
      case _ => ""
    })

    val fullValue: String = s"$major.$minor"

    override def toString: String = value
  }
}
