package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.SignUpEvent
import com.mohiva.play.silhouette.api.Silhouette

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.SignUpForm
import forms.SignUpForm.Data

import models.User
import models.services.AuthTokenService
import models.services.UserService

import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result

import scala.concurrent.Future

import utils.auth.DefaultEnv

/**
 * The `Sign Up` controller.
 *
 * @param messagesApi            The Play messages API.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository implementation.
 * @param authTokenService       The auth token service implementation.
 * @param avatarService          The avatar service implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param mailerClient           The mailer client.
 * @param webJarAssets           The webjar assets implementation.
 */
class SignUpController @Inject() (
    val messagesApi: MessagesApi,
    silhouette: Silhouette[DefaultEnv],
    userService: UserService,
    authInfoRepository: AuthInfoRepository,
    authTokenService: AuthTokenService,
    avatarService: AvatarService,
    passwordHasherRegistry: PasswordHasherRegistry,
    mailerClient: MailerClient,
    implicit val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport {

  /**
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.silhouette.signUp(SignUpForm.form)))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit = silhouette.UnsecuredAction.async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.silhouette.signUp(form))),
      data => {
        val result = Redirect(routes.SignUpController.view()).flashing("info" -> Messages("sign.up.email.sent", data.email))
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => processAlreadySignedUp(user, result, data, request)
          case None => processSignUp(result, data, loginInfo, request)
        }
      }
    )
  }

  private def processAlreadySignedUp(user: User, result: Result, data: SignUpForm.Data, request: Request[AnyContent]) = {
    val url = routes.SignInController.view().absoluteURL()(request)
    mailerClient.send(Email(
      subject = Messages("email.already.signed.up.subject"),
      from = Messages("email.from"),
      to = Seq(data.email),
      bodyText = Some(views.txt.silhouette.emails.alreadySignedUp(user, url).body),
      bodyHtml = Some(views.html.silhouette.emails.alreadySignedUp(user, url).body)
    ))

    Future.successful(result)
  }

  private def processSignUp(result: Result, data: Data, loginInfo: LoginInfo, request: Request[AnyContent]) = {
    val authInfo = passwordHasherRegistry.current.hash(data.password)
    val user = User(
      userID = UUID.randomUUID(),
      loginInfo = loginInfo,
      firstName = Some(data.firstName),
      lastName = Some(data.lastName),
      fullName = Some(data.firstName + " " + data.lastName),
      email = Some(data.email),
      avatarURL = None,
      activated = false
    )
    for {
      avatar <- avatarService.retrieveURL(data.email)
      user <- userService.save(user.copy(avatarURL = avatar))
      authInfo <- authInfoRepository.add(loginInfo, authInfo)
      authToken <- authTokenService.create(user.userID)
    } yield {
      val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL()(request)
      mailerClient.send(Email(
        subject = Messages("email.sign.up.subject"),
        from = Messages("email.from"),
        to = Seq(data.email),
        bodyText = Some(views.txt.silhouette.emails.signUp(user, url).body),
        bodyHtml = Some(views.html.silhouette.emails.signUp(user, url).body)
      ))

      silhouette.env.eventBus.publish(SignUpEvent(user, request))
      result
    }
  }
}
