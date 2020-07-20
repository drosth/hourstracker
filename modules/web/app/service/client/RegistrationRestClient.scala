package service.client

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[HttpRegistrationRestClient])
trait RegistrationRestClient {

  def getRegistrationsFor(employee: Employee): Future[Either[Failure, Seq[Registration]]]
}

class HttpRegistrationRestClient @Inject()(
                                            httpExt: HttpExt,
                                            override val playConfig: Configuration
                                          )(
                                            implicit
                                            system: ActorSystem,
                                            ec: ExecutionContext,
                                            mat: Materializer
                                          ) extends RestClient(httpExt)
  with RegistrationRestClient
  with config.Config
  with Logging
  with RegistrationModel.JsonProtocol {

  override def getRegistrationsFor(employee: Employee): Future[Either[Failure, Seq[Registration]]] =
    (for {
      registrationModels <- EitherT(get[Seq[RegistrationModel]](s"${Rest.endpoint}/registrations"))
    } yield registrationModels.map(_.convert())).value
}
