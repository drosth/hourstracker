import sbt._
import sbt.Keys.libraryDependencies

object Dependencies {

  lazy val commonDependencies: Seq[ModuleID] = Seq(
    `akka-slf4j`,
    logback
  )
  lazy val testDependencies: Seq[ModuleID] = Seq(
    `akka-http-testkit` % "test",
    `akka-stream-testkit` % "test",
    `akka-testkit` % "test",
    scalacheck % "test",
    scalatest % "test",
    `mockito-core` % "test"
  )
  lazy val cucumberDependencies: Seq[ModuleID] = Seq(
    `cucumber-core` % Test,
    `cucumber-scala` % Test,
    `cucumber-jvm` % Test,
    `cucumber-junit` % Test
  )
  val akkaHttpCorsVersion = "0.3.0"
  val akkaHttpVersion = "10.0.11"
  val akkaVersion = "2.5.19"
  val commonsIoVersion = "2.6"
  val cucumberRunnerVersion = "0.1.5"
  val cucumberVersion = "2.0.1"
  val javaxWsRsApiVersion = "2.1.1"
  val junitVersion = "4.12"
  val logbackVersion = "1.2.3"
  val mockitoVersion = "2.23.0"
  val scalaCheckVersion = "1.13.5"
  val scalaCsvVersion = "1.3.5"
  val scalaLoggingVersion = "3.7.2"
  val scalatestVersion = "3.0.5"
  val slf4jVersion = "1.7.25"
  val spdfVersion = "1.4.0"
  val sprayJsonVersion = "1.3.4"
  val swaggerAkkaHttpVersion = "2.0.0"
  val `akka-http-spray-json` = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  val `akka-http-xml` = "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion
  val `akka-http` = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val `akka-slf4j` = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val `commons-io` = "commons-io" % "commons-io" % commonsIoVersion
  val `scala-csv` = "com.github.tototoshi" %% "scala-csv" % scalaCsvVersion
  val `spray-json` = "io.spray" %% "spray-json" % sprayJsonVersion
  val `javax.ws.rs-api` = "javax.ws.rs" % "javax.ws.rs-api" % javaxWsRsApiVersion
  val `akka-http-cors` = "ch.megard" %% "akka-http-cors" % akkaHttpCorsVersion excludeAll ExclusionRule(organization = "com.typesafe.akka")
  val `swagger-akka-http` = "com.github.swagger-akka-http" %% "swagger-akka-http" % swaggerAkkaHttpVersion
  val rxscala = "io.reactivex" %% "rxscala" % "0.26.5"
  val zip4j = "net.lingala.zip4j" % "zip4j" % "1.3.2"
  val `spring-scala` = "org.springframework.scala" % "spring-scala" % "1.0.0.M2"
  val `commons-dbcp2` = "org.apache.commons" % "commons-dbcp2" % "2.7.0"

  // Cucumber libraries
  val `cucumber-core` = "io.cucumber" % "cucumber-core" % cucumberVersion
  val `cucumber-junit` = "io.cucumber" % "cucumber-junit" % cucumberVersion
  val `cucumber-jvm` = "io.cucumber" % "cucumber-jvm" % cucumberVersion
  val `cucumber-runner` = "com.waioeka.sbt" %% "cucumber-runner" % cucumberRunnerVersion
  val `cucumber-scala` = "io.cucumber" %% "cucumber-scala" % cucumberVersion

  // database
  val squeryl = "org.squeryl" %% "squeryl" % "0.9.5-7"
  val h2 = "com.h2database" % "h2" % "1.4.197"
  val `mysql-connector-java` = "mysql" % "mysql-connector-java" % "8.0.13"
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val spdf = "io.github.cloudify" %% "spdf" % spdfVersion
  val postgresql = "org.postgresql" % "postgresql" % "42.2.8"

  // Test libraries
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion
  val `mockito-core` = "org.mockito" % "mockito-core" % mockitoVersion
  val junit = "junit" % "junit" % junitVersion
  val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  val `akka-stream-testkit` = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val `akka-testkit` = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val scalacheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion
}
