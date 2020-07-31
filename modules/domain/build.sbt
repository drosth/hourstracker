Common.moduleSettings(module = "domain")

libraryDependencies ++= Common.commonDependencies ++
Seq(
  "io.spray" %% "spray-json" % "1.3.5"
)
