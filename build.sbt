name := "sbt-hourstracker"
organization := "com.example"
version := "0.1"

scalaVersion := "2.12.6"

// PROJECTS

lazy val global = project.in(file(".")).settings(settings).aggregate(rest, core)

lazy val rest = (project in file("rest"))
  .settings(
    name := "hourstracker-rest",
    settings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.`swagger-akka-http`,
      dependencies.`akka-http-spray-json`,
      dependencies.`akka-http-xml`,
      dependencies.`akka-http`,
      dependencies.`akka-stream`,
      dependencies.`javax.ws.rs-api`,
      dependencies.`akka-http-cors`,
      dependencies.`akka-http-testkit` % Test,
      dependencies.`akka-stream-testkit` % Test,
      dependencies.`akka-testkit` % Test,
      dependencies.scalatest % Test
    )
  )
  .dependsOn(core)

lazy val core = (project in file("core"))
  .enablePlugins(SbtTwirl)
  .settings(
    name := "hourstracker-core",
    settings,
    sourceDirectories in (Compile, TwirlKeys.compileTemplates) += (baseDirectory.value.getParentFile / "src" / "main" / "twirl"),
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.`commons-io`,
      dependencies.`scala-csv`,
      dependencies.`spray-json`,
      dependencies.spdf
    )
  )

// SETTINGS

lazy val settings =
  commonSettings

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions
)

lazy val dependencies =
  new {
    val akkaHttpV = "10.0.11"
    val akkaV = "2.5.6"
    val commonsIoV = "2.6"
    val logbackV = "1.2.3"
    val scalacheckV = "1.13.5"
    val scalaCsvV = "1.3.5"
    val scalaLoggingV = "3.7.2"
    val scalatestV = "3.0.4"
    val slf4jV = "1.7.25"
    val spdfV = "1.4.0"
    val sprayJsonV = "1.3.4"
    val swaggerAkkaHttpV = "2.0.0"
    val javaxWsRsApiV = "2.1.1"
    val akkaHttpCorsV = "0.3.0"

    val `akka-http-spray-json` = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV
    val `akka-http-xml` = "com.typesafe.akka" %% "akka-http-xml" % akkaHttpV
    val `akka-http` = "com.typesafe.akka" %% "akka-http" % akkaHttpV
    val `akka-slf4j` = "com.typesafe.akka" %% "akka-slf4j" % akkaV
    val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % akkaV
    val `commons-io` = "commons-io" % "commons-io" % commonsIoV
    val `scala-csv` = "com.github.tototoshi" %% "scala-csv" % scalaCsvV
    val `spray-json` = "io.spray" %% "spray-json" % sprayJsonV
    val `javax.ws.rs-api` = "javax.ws.rs" % "javax.ws.rs-api" % javaxWsRsApiV
    val `akka-http-cors` = "ch.megard" %% "akka-http-cors" % akkaHttpCorsV excludeAll ExclusionRule(organization = "com.typesafe.akka")
    val `swagger-akka-http` = "com.github.swagger-akka-http" %% "swagger-akka-http" % swaggerAkkaHttpV

    val logback = "ch.qos.logback" % "logback-classic" % logbackV
    val spdf = "io.github.cloudify" %% "spdf" % spdfV

    val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV
    val `akka-stream-testkit` = "com.typesafe.akka" %% "akka-stream-testkit" % akkaV
    val `akka-testkit` = "com.typesafe.akka" %% "akka-testkit" % akkaV
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV
    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
  }

lazy val commonDependencies = Seq(
  dependencies.`akka-slf4j`,
  dependencies.logback,
  dependencies.scalacheck % "test",
  dependencies.scalatest % "test"
)

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := false
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)
