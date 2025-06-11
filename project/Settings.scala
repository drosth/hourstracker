import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.*
import com.typesafe.sbt.packager.Keys.{daemonUser, daemonUserUid, maintainer, packageName}
import sbt.*
import sbt.Keys.*
import scoverage.ScoverageKeys.*

import scala.util.Properties

object Settings {
  val akkaVersion         = "2.8.8" // because of scala version 2.12
  val akkaHttpVersion     = "10.5.3" // because of scala version 2.12
  val akkaHttpCorsVersion = "1.2.0" // because of scala version 2.12

  lazy val akkaHttpSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "akka-http-spray-json",
        "akka-http-xml",
        "akka-http"
      ).map("com.typesafe.akka" %% _ % akkaHttpVersion) ++
      Seq(
        "ch.megard"                    %% "akka-http-cors"    % akkaHttpCorsVersion, // excludeAll ExclusionRule(organization = "com.typesafe.akka")
        "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.11.0",
        "jakarta.ws.rs"                 % "jakarta.ws.rs-api" % "4.0.0"
      )
    }
  )

  lazy val akkaLoggingSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
      )
    }
  )

  lazy val akkaStreamSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "com.typesafe.akka" %% "akka-stream" % akkaVersion
      )
    }
  )

  lazy val akkaTestSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
        "com.typesafe.akka" %% "akka-testkit"        % akkaVersion
      ).map(_ % Test)
    }
  )

  lazy val commonSettings = Seq(
    updateOptions := updateOptions.value
      .withLatestSnapshots(true)
      .withGigahorse(false),
//    scalacOptions ++= Seq(
//      "-unchecked",
//      "-feature",
//      "-language:existentials",
//      "-language:higherKinds",
//      "-language:implicitConversions",
//      "-language:postfixOps",
//      "-deprecation",
//      "-encoding",
//      "utf8"
//    ),
    libraryDependencies ++= {
      Seq(
        "com.softwaremill.quicklens" %% "quicklens"     % "1.9.12",
        "commons-codec"               % "commons-codec" % "20041127.091804",
        "commons-io"                  % "commons-io"    % "20030203.000550",
        "org.apache.commons"          % "commons-text"  % "1.13.1",
        "org.typelevel"              %% "cats-core"     % "2.13.0"
      )
    }
  )

  lazy val coverageSettings: Seq[Def.Setting[_]] = Seq(
    coverageMinimumStmtTotal := 100,
    coverageMinimumBranchTotal := 100,
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    coverageExcludedFiles := s"${sourceManaged.value.toString}/.*;.*/target/.*/twirl/.*",
    //    coverageExcludedPackages := """.*\.config\..*;nl\.dpes\.b2b\.v[0-9]+\..*;nl\.dpes\.b2b\.salesforce\.v[0-9]+\..*;nl\.dpes\.b2b\.jobmanager\.v[0-9]+\..*"""
    libraryDependencies += "org.scoverage" %% "scalac-scoverage-runtime" % "2.0.7" % Test
  )

  lazy val cucumberSettings: Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= {
      val cucumberVersion = "7.15.0"

      (Seq(
        // see also: https://github.com/sbt/junit-interface
        "com.github.sbt" % "junit-interface" % "0.13.3",
        // A reporter plug-in for pretty Cucumber reports
        // See also: https://gitlab.com/jamietanna/cucumber-reporting-plugin
        "me.jvt.cucumber" % "reporting-plugin" % "7.11.0",
        "io.cucumber"    %% "cucumber-scala"   % "8.28.0"
      ) ++
        Seq(
          "io.cucumber" % "cucumber-core",
          "io.cucumber" % "cucumber-junit",
          "io.cucumber" % "cucumber-jvm"
        ).map(_ % cucumberVersion))
        .map(_ % Test)
    }
  )

  lazy val databaseSettings = Seq(
    libraryDependencies ++= {
      val squerylVersion    = "0.9.18"
      val h2Version         = "2.2.224"
      val mysqlVersion      = "8.0.33"
      val postgresqlVersion = "42.7.1"

      Seq(
        "org.squeryl"   %% "squeryl"              % squerylVersion,
        "com.h2database" % "h2"                   % h2Version % Test,
        "mysql"          % "mysql-connector-java" % mysqlVersion,
        "org.postgresql" % "postgresql"           % postgresqlVersion
      ) ++
      Seq(
        "org.flywaydb"       % "flyway-core"   % "9.22.3", // "10.6.0"
        "org.apache.commons" % "commons-dbcp2" % "2.13.0"
      )
    }
  )

  lazy val defaultProjectSettings: Seq[Def.Setting[_]] = Seq(
    organization := Common.organization,
    organizationHomepage := Common.organizationHomepage,
    organizationName := Common.organizationName,
    resolvers ++= Common.resolvers,
    startYear := Common.startYear,
    commands ++= Commands.additionalCommands,
    publishArtifact := false
  )

  lazy val defaultScalaProjectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := Common.scalaVersion,
    libraryDependencies += "org.scala-lang" % "scala-library" % Common.scalaVersion,
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.3" cross CrossVersion.full)
  ) ++
    commonSettings ++
    loggingSettings ++
    jsonSettings ++
    coverageSettings ++
    testSettings ++
    defaultProjectSettings

  def dockerSettings(name: String): Seq[Def.Setting[_]] = Seq(
    Docker / dockerBaseImage := "eclipse-temurin:21.0.7_6-jdk",
    Docker / daemonUserUid := Some("1001"),
    Docker / daemonUser := "daemon",
    Docker / maintainer := Common.maintainer,
    Docker / packageName := name,
    dockerAliases ++= Seq(Properties.propOrNone("build"), Some("latest")).flatten.map(tag => dockerAlias.value.withTag(Some(tag))),
    Docker / version := version.value
  )

  lazy val jsonSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "io.spray" %% "spray-json" % "1.3.6" // 1.3.4"
      )
    }
  )

  lazy val loggingSettings = {
    val slf4jVersion = "2.0.17"

    Seq(
      libraryDependencies ++=
        Seq(
          "ch.qos.logback"         % "logback-classic"          % "1.5.18",
          "org.slf4j"              % "slf4j-api"                % slf4jVersion,
          "net.logstash.logback"   % "logstash-logback-encoder" % "8.1" excludeAll ExclusionRule("ch.qos.logback"),
          "ch.qos.logback.contrib" % "logback-json-classic"     % "0.1.5" excludeAll ExclusionRule("ch.qos.logback")
        ),
      dependencyOverrides ++= Seq(
        "org.slf4j" % "slf4j-api" % slf4jVersion
      )
    )
  }

  lazy val rxscalaSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "io.reactivex" %% "rxscala" % "0.27.0"
      )
    }
  )

  lazy val scalaCsvSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "com.github.tototoshi" %% "scala-csv" % "2.0.0"
      )
    }
  )

  lazy val spdfSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "io.github.cloudify" % "spdf_2.12" % "1.4.0" excludeAll ExclusionRule(
          organization = "org.scala-lang.modules",
          name         = "scala-xml_2.12"
        )
      )
    },
    dependencyOverrides ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "2.4.0"
    )
  )

  lazy val springScalaSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "org.springframework.scala" % "spring-scala" % "1.0.0.M2"
      )
    }
  )

  lazy val testSettings = Seq(
    libraryDependencies ++= {
      val scalatestVersion  = "3.2.19"
      val mockitoVersion    = "5.18.0"
      val junitVersion      = "4.13.2"
      val scalaCheckVersion = "1.18.1"

      Seq(
        "org.scalatest"     %% "scalatest"    % scalatestVersion,
        "org.mockito"        % "mockito-core" % mockitoVersion,
        "junit"              % "junit"        % junitVersion,
        "org.scalacheck"    %% "scalacheck"   % scalaCheckVersion,
        "org.scalatestplus" %% "mockito-5-8"  % "3.2.17.0"
      ).map(_ % Test)
    }
  )

  lazy val zip4jSettings = Seq(
    libraryDependencies ++= {
      Seq(
        "net.lingala.zip4j" % "zip4j" % "2.11.5"
      )
    }
  )
}
