package de.htwg.zeta.generatorControl.actors.developer.manager

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.common.models.document.Change
import de.htwg.zeta.common.models.document.Changed
import de.htwg.zeta.common.models.document.Created
import de.htwg.zeta.common.models.document.Deleted
import de.htwg.zeta.common.models.document.Updated
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.worker.RunTimedTask

private case class ExecuteTask(task: TimedTask)

object TimedTasksManager {
  def props(worker: ActorRef, repository: Repository) = Props(new TimedTasksManager(worker, repository))
}

class TimedTasksManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  private var schedules: Map[UUID, Cancellable] = Map()

  def create(task: TimedTask) = {
    val taskDelay = Duration(task.delay, TimeUnit.MINUTES)
    val taskInterval = Duration(task.interval, TimeUnit.MINUTES)
    val schedule = context.system.scheduler.schedule(taskDelay, taskInterval, self, ExecuteTask(task))
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
      task <- repository.timedTask.read(task.id)
      filter <- repository.filter.read(task.filterId)
      generator <- repository.generator.read(task.generatorId)
      image <- repository.generatorImage.read(generator.imageId)
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