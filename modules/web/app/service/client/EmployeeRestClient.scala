package service.client

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[HttpEmployeeRestClient])
trait EmployeeRestClient {
  def getEmployee(id: String): Future[Either[Failure, Employee]]
}

class HttpEmployeeRestClient @Inject()(
                                        httpExt: HttpExt,
                                        override val playConfig: Configuration
                                      )(
                                        implicit
                                        system: ActorSystem,
                                        ec: ExecutionContext,
                                        mat: Materializer
                                      ) extends RestClient(httpExt)
  with EmployeeRestClient
  with config.Config
  with Logging
  with EmployeeModel.JsonProtocol {

  override def getEmployee(id: String): Future[Either[Failure, Employee]] =
    (for {
      employeeModel <- EitherT(get[EmployeeModel](s"${Employee.Rest.endpoint}/employee/$id"))
    } yield employeeModel.convert()).value
}
