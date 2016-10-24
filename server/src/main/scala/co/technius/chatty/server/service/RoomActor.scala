package co.technius.chatty.server.service

import akka.actor.{ Actor, ActorRef, Props }

import InternalProtocol._

class RoomActor private(name: String) extends Actor {
  val clients = collection.mutable.ArrayBuffer[(String, ActorRef)]()

  def receive = {
    case InboundMessage(username, msg) =>
      clients foreach (_._2 ! OutboundMessage(s"[$name] $username: $msg"))
    case Join(username) if !clients.contains(sender()) =>
      clients += username -> sender()
      val currentlyOnline = clients.map(_._1).mkString(", ")
      sender() ! OutboundMessage(s"[$name] Online users: $currentlyOnline")
      clients foreach (_._2 ! OutboundMessage(s"[$name] $username joined the room!"))
    case Leave(username) =>
      clients.find(_._2 == sender()).foreach { user =>
        clients -= user
      }
      clients foreach (_._2 ! OutboundMessage(s"[$name] $username left the room!"))
  }
}

object RoomActor {
  def props(name: String): Props =
    Props(new RoomActor(name))
}
