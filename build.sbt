name := "hourstracker"
organization := "com.personal"
version := "0.1"
maintainer := "h.drost@gmail.com"

scalaVersion := "2.12.17"

lazy val framework = new TestFramework("com.waioeka.sbt.runner.CucumberFramework")

enablePlugins(CucumberPlugin)
com.waioeka.sbt.CucumberPlugin.projectSettings

envFileName in ThisBuild := Option(System.getProperty("envfile")).getOrElse(".env")

// PROJECTS

lazy val root = project
  .in(file("."))
  .aggregate(rest, core, storage)
  .settings(commonSettings: _*)
  .settings(cucumberSettings: _*)
  .settings(
    scapegoatVersion in ThisBuild := "1.3.8",
    publishArtifact := false
  )

lazy val core = (project in file("core"))
  .enablePlugins(CucumberPlugin, SbtTwirl)
  .settings(commonSettings: _*)
  .settings(
    name := "hourstracker-core",
    organization := "com.personal.hourstracker",
    sourceDirectories in (Compile, TwirlKeys.compileTemplates) += (baseDirectory.value.getParentFile / "src" / "main" / "twirl"),
    libraryDependencies ++= Dependencies.commonDependencies ++ Dependencies.testDependencies ++ Dependencies.cucumberDependencies ++ Seq(
      Dependencies.`commons-io`,
      Dependencies.`scala-csv`,
      Dependencies.`spray-json`,
      Dependencies.`akka-stream`,
      Dependencies.spdf,
      Dependencies.rxscala
    ),
    cucumberSettings ++ Seq(
      CucumberPlugin.glue := "com.personal.hourstracker.stepdefinitions",
      CucumberPlugin.features := List("core/src/test/features/")
    ),
    unmanagedClasspath in Test += baseDirectory.value / "../features"
  )

lazy val storage = (project in file("storage"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings: _*)
  .settings(
    name := "hourstracker-storage",
    organization := "com.personal.hourstracker",
    libraryDependencies ++= Dependencies.commonDependencies ++ Dependencies.testDependencies ++ Dependencies.cucumberDependencies ++ Seq(
      Dependencies.`akka-stream`,
      Dependencies.squeryl,
      Dependencies.`mysql-connector-java`,
      Dependencies.postgresql,
      Dependencies.flyway,
      Dependencies.`commons-dbcp2`,
      Dependencies.h2 % "test"
    )
  )

lazy val rest = (project in file("rest"))
  .dependsOn(core, storage)
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings: _*)
  .settings(
    name := "hourstracker-rest",
    organization := "com.personal.hourstracker",
    libraryDependencies ++= Dependencies.commonDependencies ++ Dependencies.testDependencies ++ Seq(
      Dependencies.`swagger-akka-http`,
      Dependencies.`akka-http-spray-json`,
      Dependencies.`akka-http-xml`,
      Dependencies.`akka-http`,
      Dependencies.`akka-stream`,
      Dependencies.`javax.ws.rs-api`,
      Dependencies.`akka-http-cors`,
      Dependencies.zip4j
    )
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

// SETTINGS

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  updateOptions := updateOptions.value
    .withLatestSnapshots(true)
    .withGigahorse(false),
  // archiva
  resolvers ++= Seq(
    DefaultMavenRepository,
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("public"),
    Resolver.sbtPluginRepo("releases")
  ),
  // coverage
  coverageMinimum := 80,
  coverageFailOnMinimum := false,
  coverageHighlighting := true
  //  coverageExcludedPackages := """.*\.config\..*;nl\.dpes\.b2b\.v[0-9]+\..*;nl\.dpes\.b2b\.salesforce\.v[0-9]+\..*;nl\.dpes\.b2b\.jobmanager\.v[0-9]+\..*"""
)

lazy val cucumberSettings = Seq(
  CucumberPlugin.glue := "com.personal.hourstracker.stepdefinitions",
  CucumberPlugin.features := List("./features"),
  // testOptions in Test += Tests.Argument(framework, "--tags", Option(System.getProperty("CUCUMBER_FILTER_TAG")).getOrElse("@sanitycheck")),
  testOptions in Test += Tests.Argument(framework, "--plugin", "pretty"),
  testOptions in Test += Tests.Argument(framework, "--plugin", "json:./target/cucumber.json"),
  testOptions in Test += Tests.Argument(framework, "--plugin", "html:./target/cucumber.html"),
  fork in test := true
)
