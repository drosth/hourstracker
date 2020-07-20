

enablePlugins(
  DockerPlugin,
  JavaAppPackaging,
  AshScriptPlugin
)

dockerBaseImage := "openjdk:8"

dockerExposedPorts := Seq(8100)

mappings in Universal += file("third-party/wkhtmltox_0.12.5-1.stretch_amd64.deb") -> "third-party/wkhtmltox.deb"

dockerCommands := dockerCommands.value.filterNot {
  case Cmd("USER", args@_*) if args contains "1001:0" => true
  case Cmd("USER", args@_*) if args contains "daemon" => true
  case ExecCmd("ENTRYPOINT", _) => true
  case ExecCmd("CMD", args@_*) if args.isEmpty => true
  case _ => false
}

dockerCommands ++= Seq(
  Cmd("RUN", "apt-get -q update && apt-get install -qy --no-install-recommends xfonts-base xfonts-75dpi && apt-get -q clean update"),
  Cmd("RUN", """apt-get -y install "/opt/docker/third-party/wkhtmltox.deb""""),
  Cmd("RUN", "apt-get install -f"),
  Cmd("USER", (daemonUser in Docker).value),
  ExecCmd("ENTRYPOINT", "/opt/docker/bin/hourstracker-rest"),
  ExecCmd("CMD")
)
