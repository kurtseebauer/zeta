package de.htwg.zeta.server.controller.webpage

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModelShortInfo
import de.htwg.zeta.common.models.modelDefinitions.model.ModelShortInfo
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedEntityPersistence
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.ws.WSClient
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

class WebpageController @Inject()(
    modelEntityRepo: AccessRestrictedEntityPersistence[ModelEntity],
    metaModelEntityRepo: AccessRestrictedEntityPersistence[MetaModelEntity],
    ws: WSClient
) extends Controller {


  def index(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Redirect(routes.ScalaRoutes.getOverviewNoArgs())
  }

  private def getMetaModels[A](request: SecuredRequest[ZetaEnv, A]): Future[Seq[MetaModelShortInfo]] = {
    val repo = metaModelEntityRepo.restrictedTo(request.identity.id)
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.toList.map(repo.read)).map(_.map(entity => {
        MetaModelShortInfo(entity.id, entity.metaModel.name)
      }))
    }
  }

  private def getModels[A](metaModelId: UUID, request: SecuredRequest[ZetaEnv, A]): Future[Seq[ModelShortInfo]] = {
    val repo = modelEntityRepo.restrictedTo(request.identity.id)
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.toList.map(repo.read)).map(_.filter(_.model.metaModelId == metaModelId).map(entity => {
        ModelShortInfo(entity.id, entity.model.metaModelId, entity.model.name)
      }))
    }
  }

  def diagramsOverviewShortInfo(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val result = for {
      metaModels <- getMetaModels(request)
    } yield {
      Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity), metaModels, None, Seq[ModelShortInfo]()))
    }

    result.recover {
      case e: Exception => BadRequest(e.getMessage)
    }

  }

  def diagramsOverview(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    if (id == null) {
      diagramsOverviewShortInfo(request)
    } else {
      val result: Future[Result] = for {
        metaModels <- getMetaModels(request)
        models <- getModels(id, request)
        metaModel <- metaModelEntityRepo.restrictedTo(request.identity.id).read(id)
      } yield {
        Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity), metaModels, Some(metaModel), models))
      }

      result.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

}

