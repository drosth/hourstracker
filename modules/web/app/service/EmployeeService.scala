package service

import scala.concurrent.{ExecutionContext, Future}

class EmployeeService @Inject()(client: EmployeeRestClient, override val playConfig: Configuration)(
  implicit
  system: ActorSystem,
  ec: ExecutionContext,
  mat: Materializer
) extends config.Config {

  def getEmployee(id: String): Future[Either[Failure, Employee]] = {
    println(s"getEmployee: '$id'")
    client.getEmployee(id)
  }
}
