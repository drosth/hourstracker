resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("public"),
//  Resolver.sonatypeOssRepos("public"),
  Resolver.sbtPluginRepo("releases")
)


dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

// Create a über JAR of your project with all of its dependencies
// see also: https://github.com/sbt/sbt-assembly
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.5")

// Code formatter for Scala
// see also: https://github.com/scalameta/sbt-scalafmt
// see also: https://scalameta.org/scalafmt/docs/installation.html#sbt
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

// integrates the scapegoat static code analysis library
// see also: https://github.com/scapegoat-scala/sbt-scapegoat
//addSbtPlugin("com.sksamuel.scapegoat" %% "scalac-scapegoat-plugin" % "2.1.3")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.2.2") // Verify latest in badge above

// Display your sbt project's dependency updates
// see also: https://github.com/rtimush/sbt-updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")

// SBT native packager lets you build application packages in native formats.
// see also: https://github.com/sbt/sbt-native-packager
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.16")

// Twirl templating
// see also: https://github.com/playframework/twirl
addSbtPlugin("com.typesafe.play" % "sbt-twirl" % "1.6.4")

// provides a new sbt command, allowing you to run just your Cucumber tests using 'sbt cucumber'
// see also: https://github.com/sbt/sbt-cucumber
addSbtPlugin("com.waioeka.sbt" % "cucumber-plugin" % "0.3.1")

// see also: https://get-coursier.io/docs/sbt-coursier
//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "2.0.8")

// enabling a super-fast development turnaround for your Scala applications.
// features:
// - Starting and stopping your application in the background of your interactive SBT shell (in a forked JVM)
// - Triggered restart: automatically restart your application as soon as some of its sources have been changed
// see also: https://github.com/spray/sbt-revolver
addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")

//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
//addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
//addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// Allows projects to monitor dependent libraries for known, published vulnerabilities (e.g. CVEs)
// see also: https://github.com/albuch/sbt-dependency-check
addSbtPlugin("net.vonbuchholtz" % "sbt-dependency-check" % "5.1.0")

// sbt-scoverage is a plugin for SBT that integrates the scoverage code coverage library.
// see also: https://github.com/scoverage/sbt-scoverage
// see also: https://github.com/scoverage/scalac-scoverage-plugin
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.9")

// desugars for-comprehensions:
//  - removing the tailing `withFilter`
//  - deconstruct returned tuples
//  - support implicits
// see also: https://github.com/oleg-py/better-monadic-for
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

// a list of recommended scalac options
// see also: https://github.com/DavidGregory084/sbt-tpolecat
//addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.3.3")

// Detects common Scala code smells
// see also: https://www.wartremover.org/
//addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.6")

// sbt plugin to load environment variables from .env into the JVM System Environment for local development.
// see also: https://github.com/mefellows/sbt-dotenv
addSbtPlugin("nl.gn0s1s" % "sbt-dotenv" % "3.0.0")

addDependencyTreePlugin
