package service.client

import scala.concurrent.{ExecutionContext, Future}

abstract class RestClient(
                           httpExt: HttpExt
                         )(
                           implicit
                           system: ActorSystem,
                           ec: ExecutionContext,
                           mat: Materializer
                         ) extends SprayJsonSupport {

  def get[T: RootJsonFormat](endpoint: String): Future[Either[String, T]] =
    (for {
      getRequest <- EitherT.fromEither[Future](getRequest(endpoint).asRight[String])
      response <- sendRequest(getRequest)
      responseEntity <- EitherT(handleGetResponse[T](response))
    } yield responseEntity).value

  private def getRequest(endpoint: String): HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = Uri(endpoint)
  )

  private def sendRequest(request: HttpRequest): EitherT[Future, String, HttpResponse] = {
    println(s"sending request: '$request'")
    EitherT(
      httpExt.singleRequest(request).map(_.asRight[String])
    )
  }

  private def handleGetResponse[R: RootJsonFormat](response: HttpResponse): Future[Either[String, R]] = {
    println(s"response: '$response'")
    response match {
      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        println(s"response.entity: '$entity'")
        Unmarshal(entity.withContentType(ContentTypes.`application/json`)).to[R].map(_.asRight[String])

      case HttpResponse(StatusCodes.NotFound, _, _, _) =>
        Future.successful(s"Could not find resource".asLeft[R])
    }
  }
}
