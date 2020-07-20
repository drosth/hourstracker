package config

trait Config {
  val playConfig: Configuration

  object Environments extends Enumeration {
    type Environment = Value

    val Development: Value = Value("development")
    val Acceptance: Value = Value("acceptance")
    val Production: Value = Value("production")
  }

  object Rest {
    val host: String = playConfig.get[String]("registration.rest.host")
    val port: Int = playConfig.get[Int]("registration.rest.port")
    val basePath: String = playConfig.get[String]("registration.rest.basePath")
    val endpoint: String = s"http://$host:$port/$basePath"
    println(s"Rest.endpoint - $endpoint")
  }

  object Employee {

    object Rest {
      val host: String = playConfig.get[String]("employee.rest.host")
      val port: Int = playConfig.get[Int]("employee.rest.port")
      val basePath: String = playConfig.get[String]("employee.rest.basePath")
      val endpoint: String = s"http://$host:$port/$basePath"
      println(s"Employee.endpoint - $endpoint")
    }

  }

  val environment: Environments.Environment = Environments.withName(playConfig.get[String]("environment"))
}

class InjectableConfiguration @Inject()(override val playConfig: Configuration) extends Config
