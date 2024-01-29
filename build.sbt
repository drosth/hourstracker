name := "hourstracker"
organization := "com.personal"

//lazy val framework = new TestFramework("com.waioeka.sbt.runner.CucumberFramework")

//enablePlugins(CucumberPlugin)
//com.waioeka.sbt.CucumberPlugin.projectSettings

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / evictionErrorLevel := Level.Info

ThisBuild / envFileName := Option(System.getProperty("envfile")).getOrElse(".env")

lazy val root = project
  .in(file("."))
  .aggregate(api, core, storage)
  .settings(Settings.defaultProjectSettings)
//  .settings(
//    ThisBuild / versionScheme := Some("early-semver")
//  )

lazy val core = (project in file("modules/core"))
  .enablePlugins(CucumberPlugin, SbtTwirl)
  .settings(Settings.defaultScalaProjectSettings)
  .settings(Settings.akkaStreamSettings)
  .settings(Settings.akkaTestSettings)
  .settings(Settings.cucumberSettings)
  .settings(Settings.rxscalaSettings)
  .settings(Settings.scalaCsvSettings)
  .settings(Settings.spdfSettings)
  .settings(
    Compile / TwirlKeys.compileTemplates / sourceDirectories += (baseDirectory.value.getParentFile / "src" / "main" / "twirl"),
    CucumberPlugin.glues := List("com.personal.hourstracker.stepdefinitions"),
    CucumberPlugin.features := List("core/src/test/features/"),
    Test / unmanagedClasspath += baseDirectory.value / "../features",
    publishArtifact := true
  )

lazy val storage = (project in file("modules/storage"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(Settings.defaultScalaProjectSettings)
  .settings(Settings.akkaStreamSettings)
  .settings(Settings.databaseSettings)
  .settings(
    publishArtifact := true
  )

lazy val api = project
  .in(file("modules/api"))
  .aggregate(rest)
  .settings(Settings.defaultProjectSettings)

lazy val rest = (project in file("modules/api/rest"))
  .dependsOn(core, storage)
  .enablePlugins(JavaAppPackaging)
  .settings(Settings.defaultScalaProjectSettings)
  .settings(Settings.akkaHttpSettings)
  .settings(Settings.akkaStreamSettings)
  .settings(Settings.jsonSettings)
  .settings(Settings.zip4jSettings)
  .settings(Settings.akkaTestSettings)
  .settings(
    publishArtifact := true
  )


