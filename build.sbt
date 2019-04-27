name := "hourstracker"
organization := "com.personal"
version := "0.1"
maintainer := "h.drost@gmail.com"

scalaVersion := "2.12.7"

lazy val framework = new TestFramework("com.waioeka.sbt.runner.CucumberFramework")

enablePlugins(CucumberPlugin)
com.waioeka.sbt.CucumberPlugin.projectSettings

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
      libraryDependencies ++= commonDependencies ++ testDependencies ++ cucumberDependencies ++ Seq(
        dependencies.`commons-io`,
        dependencies.`scala-csv`,
        dependencies.`spray-json`,
        dependencies.`akka-stream`,
        dependencies.spdf,
        dependencies.rxscala
      ),
      cucumberSettings ++ Seq(
        CucumberPlugin.glue := "com.personal.hourstracker.stepdefinitions",
        CucumberPlugin.features := List("core/src/test/features/"),
      ),
      unmanagedClasspath in Test += baseDirectory.value / "../features",
    )

lazy val storage = (project in file("storage"))
    .dependsOn(core % "compile->compile;test->test")
    .settings(commonSettings: _*)
    .settings(
      name := "hourstracker-storage",
      organization := "com.personal.hourstracker",
      libraryDependencies ++= commonDependencies ++ testDependencies ++ cucumberDependencies ++ Seq(
        dependencies.`akka-stream`,
        dependencies.squeryl,
        dependencies.`mysql-connector-java`,
        dependencies.h2
      )
    )

lazy val rest = (project in file("rest"))
  .dependsOn(core, storage)
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings: _*)
  .settings(
    name := "hourstracker-rest",
    organization := "com.personal.hourstracker",
    libraryDependencies ++= commonDependencies ++ testDependencies ++ Seq(
      dependencies.`swagger-akka-http`,
      dependencies.`akka-http-spray-json`,
      dependencies.`akka-http-xml`,
      dependencies.`akka-http`,
      dependencies.`akka-stream`,
      dependencies.`javax.ws.rs-api`,
      dependencies.`akka-http-cors`,
      dependencies.zip4j
    )
  )

lazy val dependencies =
  new {
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

    // Test libraries
    val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion
    val `mockito-core` = "org.mockito" % "mockito-core" % mockitoVersion
    val junit = "junit" % "junit" % junitVersion
    val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
    val `akka-stream-testkit` = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
    val `akka-testkit` = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  }

lazy val commonDependencies = Seq(
  dependencies.`akka-slf4j`,
  dependencies.logback
)

lazy val testDependencies = Seq(
  dependencies.`akka-http-testkit` % "test",
  dependencies.`akka-stream-testkit` % "test",
  dependencies.`akka-testkit` % "test",
  dependencies.scalacheck % "test",
  dependencies.scalatest % "test",
  dependencies.`mockito-core` % "test"
)

lazy val cucumberDependencies = Seq(
  dependencies.`cucumber-core` % Test,
  dependencies.`cucumber-scala` % Test,
  dependencies.`cucumber-jvm` % Test,
  dependencies.`cucumber-junit` % Test
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
