package controllers

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(
                                registrationService: RegistrationService,
                                employeeService: EmployeeService,
                                val controllerComponents: ControllerComponents,
                                langs: Langs
                              )(
                                implicit ec: ExecutionContext
                              ) extends BaseController
  with I18nSupport
  with Logging {

  implicit val lang: Lang = langs.availables.head
  implicit val messages: Messages = MessagesImpl(lang, messagesApi)

  def dashboard() = Action.async { implicit request: Request[AnyContent] =>
    request.cookies.get("token").map(_.value) match {
      case None =>
        logger.warn("Unauthorized !!")
        Future.successful(Unauthorized("Oops"))
      case Some(token) =>
        (for {
          employee <- EitherT(employeeService.getEmployee(token))
          registrations <- EitherT(registrationService.getRegistrationsFor(employee))
        } yield (employee, registrations)).value.map {
          case Left(msg) => InternalServerError(msg)
          case Right(tups) =>
            Ok(views.html.pages.dashboard(tups._1.convert))
        }
    }
  }
}
