package models.persistence


import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.concurrent.ExecutionContextExecutor

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import models.document.Document
import models.persistence.actor.DocumentAccessorActor


/**
 */
class DatabaseAccessor[T <: Document](private val actor: Future[ActorRef], private val system: ActorSystem) {
  private val timeout = Timeout(1, TimeUnit.SECONDS)
  private val dispatcher: ExecutionContextExecutor = system.dispatcher


  def ReadDocument: Future[T] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.ReadDocument)(timeout).map {
      case DocumentAccessorActor.ReadingDocumentSucceed(doc: T) => doc
      case DocumentAccessorActor.ReadingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

  def CreateDocument(doc: T): Future[Any] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.CreateDocument)(timeout).map {
      case DocumentAccessorActor.CreatingDocumentSucceed =>
      case DocumentAccessorActor.CreatingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

  def UpdateDocument(doc: T): Future[Any] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.UpdateDocument)(timeout).map {
      case DocumentAccessorActor.UpdatingDocumentSucceed =>
      case DocumentAccessorActor.UpdatingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

  def DeleteDocument: Future[Any] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.DeleteDocument)(timeout).map {
      case DocumentAccessorActor.DeletingDocumentSucceed =>
      case DocumentAccessorActor.DeletingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

}
