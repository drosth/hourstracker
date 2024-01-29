import sbt._

object Common {
//  val buildNumber: String = Option(System.getProperty("build")).getOrElse("SNAPSHOT")
  val buildSuffix: String = Option(System.getProperty("build")).getOrElse("0-SNAPSHOT")

  lazy val organization = "com.personal.hourstracker"
  lazy val organizationName = "DrostIT"
  lazy val maintainer = "DrostIT"

  lazy val organizationHomepage: Option[URL] = None
  lazy val startYear: Option[Int] = Some(2021)
  lazy val scalaVersion = "2.12.18"

  lazy val publishTo: Option[MavenRepository] = None

  lazy val resolvers: Seq[Resolver] = Seq(
    DefaultMavenRepository,
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("public"),
    Resolver.sbtPluginRepo("releases")
  )
}
