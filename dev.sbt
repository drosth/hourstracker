import scala.sys.process._

lazy val startContainers =
  taskKey[Unit]("Start the necessary Docker containers for DEV")

startContainers := {
  streams.value.log.info(
    "docker-compose up -d" !!
  )
}
