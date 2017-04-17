package actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import models.document.Change
import models.document.TimedTask
import models.worker.RunTimedTask
import models.document._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

private case class ExecuteTask(task: TimedTask)

object TimedTasksManager {
  def props(worker: ActorRef, repository: Repository) = Props(new TimedTasksManager(worker, repository))
}

class TimedTasksManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  private var schedules: Map[String, Cancellable] = Map()

  def create(task: TimedTask) = {
    val schedule = context.system.scheduler.schedule(task.delay.minutes, task.interval.minutes, self, ExecuteTask(task))
    schedules += (task.id -> schedule)
  }

  def update(task: TimedTask) = {
    schedules.get(task.id) match {
      case Some(x) => x.cancel()
      case None => // task did not exist
    }
    create(task)
  }

  def remove(task: TimedTask) = {
    schedules.get(task.id) match {
      case Some(x) => x.cancel()
      case None => // task did not exist
    }
    schedules -= task.id
  }

  def executeTask(task: TimedTask) = {
    val result = for {
      task <- repository.get[TimedTask](task.id)
      filter <- repository.get[Filter](task.filter)
      generator <- repository.get[Generator](task.generator)
      image <- repository.get[GeneratorImage](generator.image)
    } yield RunTimedTask(task.id, generator.id, filter.id, image.dockerImage)

    result.map {
      job => worker ! job
    }.recover {
      case e: Exception => log.error(e.toString)
    }
  }

  def receive = {
    case Changed(task: TimedTask, change: Change) => change match {
      case Created => create(task)
      case Updated => update(task)
      case Deleted => remove(task)
    }
    case ExecuteTask(task) => executeTask(task)
    case _ =>
  }
}
