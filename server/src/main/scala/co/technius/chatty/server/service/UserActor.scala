package co.technius.chatty.server.service

import akka.actor.{ Actor, ActorRef, Props }
import akka.stream.actor.{ ActorPublisher, ActorSubscriber, WatermarkRequestStrategy }
import akka.stream.actor.{ ActorSubscriberMessage => Sub }
import akka.stream.actor.{ ActorPublisherMessage => Pub }

import InternalProtocol._

class UserActor private(name: String, roomActor: ActorRef) extends Actor
    with ActorSubscriber
    with ActorPublisher[String] {

  val requestStrategy = new WatermarkRequestStrategy(100)

  val msgQueue = collection.mutable.Queue[String]()
  def receive = {
    case Sub.OnNext(msg: String) =>
      roomActor ! InboundMessage(name, msg)
    case Pub.Request(n) => flushQueue(n)
    case OutboundMessage(msg) =>
      msgQueue += msg
      if (isActive && totalDemand > 0) {
        flushQueue(totalDemand)
      }
    case Sub.OnComplete =>
      roomActor ! Leave(name)
  }

  def flushQueue(num: Long): Unit = {
    var cur = 0
    while (cur < num && msgQueue.size > 0) {
      val msg = msgQueue.dequeue()
      onNext(msg)
      cur = cur + 1
    }
  }
}

object UserActor {
  def props(name: String, roomActor: ActorRef): Props =
    Props(new UserActor(name, roomActor))
}
