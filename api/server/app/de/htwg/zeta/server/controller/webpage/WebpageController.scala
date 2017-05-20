package de.htwg.zeta.server.controller.webpage

import javax.inject.Inject

import scala.concurrent.Promise
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import models.document.AllMetaModels
import models.document.AllModels
import models.document.MetaModelEntity
import models.document.ModelEntity
import models.document.Repository
import models.document.http.HttpRepository
import models.modelDefinitions.metaModel.MetaModelShortInfo
import models.modelDefinitions.model.ModelShortInfo
import play.api.Logger
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Result
import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext
import de.htwg.zeta.server.util.auth.ZetaEnv

class WebpageController @Inject()(implicit ws: WSClient, silhouette: Silhouette[ZetaEnv]) extends Controller {

  val log = Logger(this getClass() getName())

  def repository[A]()(implicit request: SecuredRequest[ZetaEnv, A]): Repository =
    new HttpRepository(request.cookies.get("SyncGatewaySession").get.value)

  def index() = silhouette.SecuredAction { implicit request =>
    Redirect("/overview")
  }

  private def getMetaModels[A]()(implicit request: SecuredRequest[ZetaEnv, A]) = {
    val p = Promise[Seq[MetaModelShortInfo]]

    repository.query[MetaModelEntity](AllMetaModels())
      .map { entity =>
        MetaModelShortInfo(entity.id, entity.name, entity.links)
      }
      .toSeq.materialize.subscribe(n => n match {
      case OnError(err) => p.failure(err)
      case OnNext(list) => p.success(list)
    })

    p.future
  }

  private def getModels[A](metaModel: String)(implicit request: SecuredRequest[ZetaEnv, A]) = {
    val p = Promise[Seq[ModelShortInfo]]

    if (metaModel != null) {
      repository.query[ModelEntity](AllModels())
        .filter { entity =>
          entity.metaModelId == metaModel
        }
        .map { entity =>
          ModelShortInfo(entity.id, entity.metaModelId, entity.model.name)
        }
        .toSeq.materialize.subscribe(n => n match {
        case OnError(err) => p.failure(err)
        case OnNext(list) => p.success(list)
      })
    } else {
      p.success(Seq())
    }
    p.future
  }

  def diagramsOverviewShortInfo(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val result = for {
      metaModels <- getMetaModels
    } yield {
      Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity), metaModels, None, Seq[ModelShortInfo]()))
    }

    result.recover {
      case e: Exception => BadRequest(e.getMessage)
    }

  }

  def diagramsOverview(uuid: String): Action[AnyContent] = {
    if (uuid == null) {
      diagramsOverviewShortInfo()
    } else {
      silhouette.SecuredAction.async { implicit request =>

        val result: Future[Result] = for {
          metaModels <- getMetaModels
          models <- getModels(uuid)
          metaModel <- repository.get[MetaModelEntity](uuid)
        } yield {
          Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity), metaModels, Some(metaModel), models))
        }

        result.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    }
  }

}
