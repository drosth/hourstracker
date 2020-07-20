

enablePlugins(
  DockerPlugin,
  JavaAppPackaging,
  AshScriptPlugin
)

dockerBaseImage := "openjdk:8"

dockerExposedPorts := Seq(9000)

dockerCommands := dockerCommands.value.filterNot {
  case Cmd("USER", args@_*) if args contains "1001:0" => true
  case Cmd("USER", args@_*) if args contains "daemon" => true
  case ExecCmd("ENTRYPOINT", _) => true
  case ExecCmd("CMD", args@_*) if args.isEmpty => true
  case _ => false
}

dockerCommands ++= Seq(
  Cmd("USER", (daemonUser in Docker).value),
  ExecCmd("ENTRYPOINT", "/opt/docker/bin/hourstracker-web"),
  ExecCmd("CMD")
)
