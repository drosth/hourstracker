import sbt.Keys.version

scalaVersion := "2.12.6"

lazy val root = (project in file("."))
    .enablePlugins(SbtTwirl)
    .settings(

      name := "hourstracker",
      version := "0.1",

      sourceDirectories in (Compile, TwirlKeys.compileTemplates) += (baseDirectory.value.getParentFile / "src" / "main" / "twirl"),

      libraryDependencies ++= Seq(
        "com.github.tototoshi" %% "scala-csv" % "1.3.5",
        "io.spray" %% "spray-json" % "1.3.4",
        "io.github.cloudify" %% "spdf" % "1.4.0",
        "commons-io" % "commons-io" % "2.6"
      )
    )
