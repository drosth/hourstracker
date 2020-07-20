package com.personal.hourstracker

package object rest {

  object Version {

    def apply(major: Int, minor: Int = 0, patch: Int = 0): Version = (major, minor, patch) match {
      case (mjr, 0, 0) => new Version(s"$mjr")
      case (mjr, mnr, 0) => new Version(s"$mjr.$mnr")
      case (mjr, mnr, p) => new Version(s"$mjr.$mnr.$p")
    }
  }

  class Version(value: String) {
    override def toString: String = value
  }

}
