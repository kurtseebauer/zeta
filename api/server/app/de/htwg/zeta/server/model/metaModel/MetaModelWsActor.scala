package de.htwg.zeta.server.model.metaModel

import java.util.UUID

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck
import de.htwg.zeta.server.model.codeEditor.MediatorMessage
import play.api.Logger
import play.api.libs.json.JsValue
import scala.language.postfixOps

object MetaModelWsActor {
  def props(out: ActorRef, metaModelUuid: UUID): Props = Props(new MetaModelWsActor(out, metaModelUuid))
}

class MetaModelWsActor(out: ActorRef, metaModelId: UUID) extends Actor {

  val log = Logger(getClass.getName)
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(metaModelId.toString, self)

  /**
   * Send an incoming WebSocket message to all other subscribed WebSocket actors.
   * Every actor, except of the broadcaster itself, forwards the received message to its client.
   */
  override def receive: Receive = {
    case msg: JsValue => mediator ! Publish(metaModelId.toString, MediatorMessage(msg, self))
    case msg: MediatorMessage => if (msg.broadcaster != self) out ! msg.msg

    case ack: SubscribeAck => log.info(s"Subscribed to ${ack.subscribe.topic}")
    case _ => log.error("Got unknown message")
  }

}
