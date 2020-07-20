import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport.{daemonUser, daemonUserUid}
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.{coverageFailOnMinimum, coverageHighlighting, coverageMinimum}

object Common {

  val `scala-version` = "2.13.2"
  val appName = "hourstracker"
  val appOrganization = "com.personal"
  val appVersion = "1.0-SNAPSHOT"
  val appMaintainer = "h.drost@gmail.com"

  val akkaHttpVersion = "10.1.11"
  val akkaVersion = "2.6.5"

  val jacksonDependencies = Seq(
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.10.1",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.10.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.1",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.1",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.1"
  )

  // Common settings for every project
  def settings(artifactGroupId: String = s"$appOrganization.$appName", artifactId: String, artifactVersion: String = appVersion) =
    Seq(
      name := s"$appName-$artifactId",
      organization := artifactGroupId,
      version := artifactVersion,
      scalaVersion := `scala-version`,
      scalacOptions ++= Seq(
        "-unchecked",
        "-feature",
        "-language:existentials",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-language:postfixOps",
        "-deprecation",
        "-encoding",
        "utf8"
      ),
      resolvers ++= Seq(
        DefaultMavenRepository,
        Resolver.typesafeRepo("releases"),
        Resolver.sonatypeRepo("public"),
        Resolver.sbtPluginRepo("releases"),
        Resolver.bintrayRepo("kpmeen", "maven"),
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
        Resolver.file("Local Ivy2 Repo", file(Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.ivyStylePatterns)
      ),
      dependencyOverrides ++= jacksonDependencies ++ Seq(
        "org.scala-lang.modules" % "scala-xml_2.13" % "1.2.0"
      ),
      // coverage
      coverageMinimum := 80,
      coverageFailOnMinimum := false,
      coverageHighlighting := true
    )

  lazy val dockerSettings = Seq(
    dockerAliases ++= Set(
      dockerAlias.value.withTag(Some("latest")),
      dockerAlias.value.withTag(Some(scala.util.Properties.propOrNone("version").getOrElse("latest")))
    ).toSeq,
    daemonUserUid in Docker := Some("1001"),
    daemonUser in Docker := "daemon",
    dockerBaseImage := "openjdk:8-jre-alpine",
    maintainer in Docker := maintainer.value,
    packageName in Docker := s"$appName-${name.value}",
    version in Docker := version.value
  )

  // Settings for every module, i.e. for every subproject
  def moduleSettings(module: String) =
    settings(artifactId = module)

  def moduleSettings(artifactGroupId: String, artifactId: String, artifactVersion: String) =
    settings(artifactGroupId, artifactId, artifactVersion)

  val commonTestDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.1.1" % Test,
    "org.mockito" %% "mockito-scala" % "1.14.0" % Test, // see also: https://github.com/mockito/mockito-scala
    "com.h2database" % "h2" % "1.4.200" % Test
  )

  val loggingDependencies: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "ch.qos.logback.contrib" % "logback-jackson" % "0.1.5",
    "ch.qos.logback.contrib" % "logback-json-classic" % "0.1.5"
  )

  val commonDependencies: Seq[ModuleID] = loggingDependencies ++ commonTestDependencies ++ Seq(
    "org.typelevel" %% "cats-core" % "2.1.1"
  )

  val akkaTestDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  )

  val bootstrapDependencies: Seq[ModuleID] = Seq(
    // bootstrap
    "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
    "org.webjars" % "requirejs" % "2.3.6"
  )

  val playDependencies: Seq[ModuleID] = commonDependencies ++ jacksonDependencies ++ bootstrapDependencies ++ Seq(
    play.sbt.PlayImport.guice exclude("com.google.guava", "guava"),
    "com.google.guava" % "guava" % "28.1-jre",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
  )

  val playSlickDependencies: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.jdbc,
    "com.typesafe.play" %% "play-slick" % "5.0.0"
  )

  val akkaDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-coordination" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  )

  val akkaHttpDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "ch.megard" %% "akka-http-cors" % "0.4.3" excludeAll ExclusionRule(organization = "com.typesafe.akka")
  )

  val akkaPersistenceDependencies = Seq(
    "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
    "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.5.3",
    "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.15.2" % Test,
    // local levelDB stores
    "org.iq80.leveldb" % "leveldb" % "0.12",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    // Cassandra
    "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.102",
    "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.102" % Test
  )
}
