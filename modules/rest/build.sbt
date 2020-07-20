Common.moduleSettings(module = "rest")

maintainer in Docker := Common.appMaintainer

packageName in Docker := name.value

libraryDependencies ++= Common.akkaHttpDependencies ++
  Common.akkaDependencies ++
  Common.commonDependencies ++
  Common.akkaTestDependencies ++
  Seq(
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.0.5",
    "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1",
    "net.lingala.zip4j" % "zip4j" % "2.5.2"
  )
