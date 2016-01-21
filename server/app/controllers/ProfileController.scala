package controllers

import javax.inject.Inject

import models.SecureSocialUser
import play.api.data.Form
import play.api.data.Forms.{email, mapping, text}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import util.definitions.UserEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._

case class ProfileFormData(firstName: String, lastName: String, fullName: String, eMail: String)

class ProfileController @Inject()(override implicit val env: UserEnvironment, val messagesApi: MessagesApi)
  extends securesocial.core.SecureSocial with I18nSupport {


  val profileDataForm = Form(
    mapping(
      "firstName" -> text,
      "lastName" -> text,
      "fullName" -> text,
      "eMail" -> email
    )(ProfileFormData.apply)(ProfileFormData.unapply)
  )

  def overview() = SecuredAction { implicit request =>
    Ok(views.html.webpage.ProfileOverview.render(request.user))
  }

  def edit() = SecuredAction { implicit request =>
    val filledForm = profileDataForm.fill(profileData(request.user))
    Ok(views.html.webpage.ProfileEdit.render(request.user, filledForm, implicitly[Messages]))
  }

  def saveChanges() = SecuredAction { implicit request =>
    profileDataForm.bindFromRequest.fold(
      formWithErrors => {
        val filledForm = formWithErrors.fill(profileData(request.user))
        BadRequest(views.html.webpage.ProfileEdit.render(request.user, filledForm, implicitly[Messages]))
      },
      userData => {
        val user = Await.result(env.userService.updateProfile(request.user,
          firstName = if (userData.firstName.nonEmpty) Some(userData.firstName) else None,
          lastName = if (userData.lastName.nonEmpty) Some(userData.lastName) else None,
          fullName = if (userData.fullName.nonEmpty) Some(userData.fullName) else None,
          eMail = if (userData.eMail.nonEmpty) Some(userData.eMail) else None
        ), 5 seconds)

        request.authenticator.updateUser(user)
        Redirect(controllers.routes.ProfileController.overview())
      })

  }

  def profileData(user: SecureSocialUser) = {
    ProfileFormData(
      firstName = user.profile.firstName.getOrElse(""),
      lastName = user.profile.lastName.getOrElse(""),
      fullName = user.profile.fullName.getOrElse(""),
      eMail = user.profile.email.getOrElse("")
    )
  }

}