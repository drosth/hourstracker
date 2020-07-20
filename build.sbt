name := Common.appName
organization := Common.appOrganization
version := Common.appVersion
maintainer := Common.appMaintainer

scalaVersion := Common.`scala-version`

envFileName in ThisBuild := Option(System.getProperty("envfile")).getOrElse(".env")

lazy val root = (project in file(".")).aggregate(core, storage, rest, web)
  .settings(
    publishArtifact := false
  )

lazy val domain = (project in file("modules/domain"))

lazy val core = (project in file("modules/core")).dependsOn(domain).enablePlugins(SbtTwirl)

lazy val storage = (project in file("modules/storage")).dependsOn(core % "compile->compile;test->test")

lazy val rest = (project in file("modules/rest"))
  .dependsOn(core, storage)
  .settings(Common.dockerSettings: _*)

lazy val web = (project in file("modules/web"))
  .dependsOn(rest)
  .enablePlugins(PlayScala)
  .settings(Common.dockerSettings: _*)


import scala.sys.process._

lazy val dcupLocal = taskKey[Unit]("(re)start docker containers using 'docker-compose up'")
dcupLocal := {
  (publishLocal in Docker).value
  s"docker-compose -f docker-compose.yml -f docker/docker-compose.local.yml up -d".!!
  s"docker-compose -f docker-compose.yml -f docker/docker-compose.local.yml logs -f".!!
}

lazy val dcupHeroku = taskKey[Unit]("(re)start docker containers using 'docker-compose up'")
dcupHeroku := {
  (publishLocal in Docker).value
  s"docker-compose -f docker-compose.yml -f docker/docker-compose.heroku.yml up -d".!!
  s"docker-compose -f docker-compose.yml -f docker/docker-compose.heroku.yml logs -f".!!
}
