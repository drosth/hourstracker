import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

enablePlugins(
  DockerPlugin,
  JavaAppPackaging,
  AshScriptPlugin
)

dockerCommands := dockerCommands.value.filterNot {
  case Cmd("USER", args @ _*) if args contains "1001:0" => true
  case Cmd("USER", args @ _*) if args contains "daemon" => true
  case ExecCmd("ENTRYPOINT", _)                         => true
  case ExecCmd("CMD", args @ _*) if args.isEmpty        => true
  case _                                                => false
}

dockerBaseImage := "openjdk:11-jre"

dockerExposedPorts := Seq(8080)

dockerAliases ++= Set(
  dockerAlias.value.withTag(Some("latest")),
  dockerAlias.value.withTag(Some(scala.util.Properties.propOrNone("version").getOrElse("latest")))
).toSeq

maintainer in Docker := "h.drost@gmail.com"

packageName in Docker := name.value

//Universal / mappings += file("./third-party/wkhtmltox_0.12.5-1.stretch_amd64.deb") -> "/tmp/wkhtmltox_0.12.5-1.stretch_amd64.deb"

dockerCommands ++= Seq(
  Cmd("RUN", "apt-get -q update"),
  Cmd("RUN", "apt-get install -qy --no-install-recommends xfonts-base xfonts-75dpi"),
  Cmd("RUN", "apt-get -q clean update"),

  Cmd("RUN", "apt-get -qy install '/opt/docker/third-party/wkhtmltox_0.12.5-1.stretch_amd64.deb'"),
  Cmd("RUN", "apt-get install -f"),

  Cmd("USER", ( Docker / daemonUser ).value),
  ExecCmd("ENTRYPOINT", "/opt/docker/bin/hourstracker-rest"),
  ExecCmd("CMD")
)

