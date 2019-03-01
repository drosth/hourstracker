enablePlugins(
  DockerPlugin,
  JavaAppPackaging,
  AshScriptPlugin
)

dockerBaseImage := "openjdk:8-jre-alpine"

dockerAliases ++= Set(
  dockerAlias.value.withTag(Some("latest")),
  dockerAlias.value.withTag(Some(scala.util.Properties.propOrNone("version").getOrElse("latest")))
).toSeq

maintainer in Docker := "h.drost@gmail.com"
packageName in Docker := name.value
