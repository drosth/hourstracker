package com.personal.hourstracker.config

sealed abstract class Environment(val name: String, val abbreviation: String)

object Environment {

  def apply(name: String): Environment = name match {
    case Development.name => Development
    case Testing.name => Testing
    case Acceptance.name => Acceptance
    case Production.name => Production
    case Heroku.name => Heroku
    case _ => throw UnknownEnvironmentException(s"Environment for “$name” does not exist")
  }

  final case class UnknownEnvironmentException(message: String) extends RuntimeException(message)

  final case object Development extends Environment("development", "dev")

  final case object Testing extends Environment("testing", "test")

  final case object Acceptance extends Environment("acceptance", "acc")

  final case object Production extends Environment("production", "pro")

  final case object Heroku extends Environment("heroku", "heroku")

}
