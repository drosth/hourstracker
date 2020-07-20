Common.moduleSettings(module = "web")

maintainer in Docker := Common.appMaintainer

packageName in Docker := name.value

libraryDependencies ++= Common.playDependencies ++ Common.akkaHttpDependencies ++ Seq(
  "org.webjars" % "bootswatch-cerulean" % "4.2.1",
  "com.personal.employee" %% "employee-domain" % "0.1",
  "com.personal.employee" %% "employee-rest" % "0.1"
)

dependencyOverrides ++= Common.jacksonDependencies ++ Seq(
  "org.scala-lang.modules" % "scala-xml_2.13" % "1.2.0",
)
