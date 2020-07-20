Common.moduleSettings(module = "storage")

libraryDependencies ++= Common.commonDependencies ++ Common.akkaDependencies ++ Seq(
  "org.squeryl" %% "squeryl" % "0.9.14",
  "mysql" % "mysql-connector-java" % "8.0.20",
  "org.postgresql" % "postgresql" % "42.2.12",
  "org.apache.commons" % "commons-dbcp2" % "2.7.0"
)
