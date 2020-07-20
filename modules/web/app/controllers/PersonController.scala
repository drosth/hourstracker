package controllers

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PersonController @Inject()(
                                  employeeService: EmployeeService,
                                  val controllerComponents: ControllerComponents,
                                  langs: Langs
                                )(
                                  implicit ec: ExecutionContext
                                ) extends BaseController
  with I18nSupport
  with Logging {

  val lang: play.api.i18n.Lang = langs.availables.head
  implicit val messages: Messages = MessagesImpl(lang, messagesApi)

  def profile() = Action.async { implicit request =>
    request.cookies.get("token").map(_.value) match {
      case None =>
        logger.warn("Unauthorized !!")
        Future.successful(Unauthorized("No token..."))
      case Some(token) =>
        (for {
          employee <- EitherT(employeeService.getEmployee(token))
        } yield employee).value.map {
          case Left(msg) => InternalServerError(msg)
          case Right(e) => Ok(views.html.pages.profile(e.convert))
        }
    }
  }
}
