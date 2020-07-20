package service

import scala.concurrent.{ExecutionContext, Future}

class RegistrationService @Inject()(client: RegistrationRestClient, override val playConfig: Configuration)(
  implicit
  system: ActorSystem,
  ec: ExecutionContext,
  mat: Materializer
) extends config.Config {

  def getRegistrationsFor(employee: Employee): Future[Either[Failure, Seq[Registration]]] = client.getRegistrationsFor(employee)
}
