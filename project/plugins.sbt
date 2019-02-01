resolvers += "spray repo" at "http://repo.spray.io"

addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.15")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.2")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.2")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.15")

addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.9") // https://github.com/sksamuel/sbt-scapegoat
