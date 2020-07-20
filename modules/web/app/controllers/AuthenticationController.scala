package controllers

import scala.concurrent.ExecutionContext

@Singleton
class AuthenticationController @Inject()(val controllerComponents: ControllerComponents, langs: Langs)(
  implicit ec: ExecutionContext
) extends BaseController
  with I18nSupport {

  val userId: play.api.mvc.Request[AnyContent] => String = request =>
    request.cookies.get("token") match {
      case Some(cookie) => cookie.value
      case None => ""
    }

  def login: Action[AnyContent] = Action {
    Redirect(routes.HomeController.dashboard())
      .withCookies(
        Cookie("token", "597d869d-2f26-4a7b-9f50-eeb97fb6444d")
      )
      .bakeCookies()
  }

  def logout: Action[AnyContent] = Action {
    Redirect("/logged-out").withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }
}
