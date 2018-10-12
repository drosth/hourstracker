import sbt._

object MyScalafmtPlugin extends AutoPlugin {
  override def trigger  = allRequirements
  override def requires = plugins.JvmPlugin
  override def buildSettings: Seq[Def.Setting[_]] =
    SettingKey[Unit]("scalafmtGenerateConfig") :=
      IO.write(
        // writes to file once when build is loaded
        file(".scalafmt.conf"),
        "maxColumn = 140".stripMargin.getBytes("UTF-8")
      )
}
