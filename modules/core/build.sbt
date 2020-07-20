Common.moduleSettings(module = "core")

sourceDirectories in(Compile, TwirlKeys.compileTemplates) += (baseDirectory.value.getParentFile / "src" / "main" / "twirl")

libraryDependencies ++= Common.akkaDependencies ++ Common.akkaTestDependencies ++ Common.commonDependencies ++
  Seq(
    //  "com.github.tototoshi" % "scala-csv_2.12" % "1.3.6",
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.0",
    "com.typesafe.akka" %% "akka-http-spray-json" % Common.akkaHttpVersion,
    "io.spray" %% "spray-json" % "1.3.5",
    //  "com.typesafe.play" %% "twirl-api" % "1.5.0"
  )
