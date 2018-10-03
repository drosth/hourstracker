name := "sbt-hourstracker"
organization := "com.example"
version := "0.1"

scalaVersion := "2.12.6"

lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion = "2.5.11"

// PROJECTS

lazy val global = project
    .in(file("."))
    .settings(settings)
    .aggregate(
      `akka-http`,
      core
    )

lazy val `akka-http` = (project in file("akka-http")).
    settings(
      name := "hourstracker-akka-http",
      settings,
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,

        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
        "org.scalatest" %% "scalatest" % "3.0.1" % Test
      )
    )
    .dependsOn(
  core
)


lazy val core = (project in file("core"))
    .enablePlugins(SbtTwirl)
    .settings(
      name := "hourstracker-core",
      settings,

      sourceDirectories in(Compile, TwirlKeys.compileTemplates) += (baseDirectory.value.getParentFile / "src" / "main" / "twirl"),

      libraryDependencies ++= Seq(
        "com.github.tototoshi" %% "scala-csv" % "1.3.5",
        "io.spray" %% "spray-json" % "1.3.4",
        "io.github.cloudify" %% "spdf" % "1.4.0",
        "commons-io" % "commons-io" % "2.6"
      )
    )

// SETTINGS

lazy val settings =
  commonSettings ++
      scalafmtSettings

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions
  //  resolvers ++= Seq(
  //    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  //    Resolver.sonatypeRepo("releases"),
  //    Resolver.sonatypeRepo("snapshots")
  //  )
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
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
)
