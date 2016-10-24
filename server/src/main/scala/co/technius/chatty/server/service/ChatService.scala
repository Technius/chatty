package co.technius.chatty.server.service

import akka.actor.ActorSystem
import akka.stream.{ FlowShape, Materializer }
import akka.stream.scaladsl._
import akka.stream.actor.{ ActorPublisher, ActorSubscriber }
import akka.http.scaladsl.model.ws.{ BinaryMessage, Message, TextMessage }

class ChatService(implicit system: ActorSystem, mat: Materializer) {
  val roomActor = system.actorOf(RoomActor.props("default"), "defaultroom")
  val roomPub =
    Source
      .fromPublisher[String](ActorPublisher(roomActor))
      .map(msg => TextMessage(Source.single(msg)))

  def flow(name: String): Flow[Message, Message, Any] = {
    val userActor = system.actorOf(UserActor.props(name, roomActor))
    roomActor.tell(InternalProtocol.Join(name), userActor)
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val userOut = b.add(Source.fromPublisher(ActorPublisher(userActor)))
      val userIn = b.add(Sink.fromSubscriber(ActorSubscriber(userActor)))

      val fromMessage = b.add(msgToStringFlow)
      val toMessage = b.add(Flow[String].map(msg => TextMessage(msg)))

      fromMessage ~> userIn
      userOut ~> toMessage

      FlowShape(fromMessage.in, toMessage.out)
    })
  }

  def msgToStringFlow: Flow[Message, String, Any] = Flow[Message].mapConcat {
    case TextMessage.Strict(msg) => msg :: Nil
    case tm: TextMessage =>
      tm.textStream.runWith(Sink.ignore)
      Nil
    case bm: BinaryMessage =>
      bm.dataStream.runWith(Sink.ignore)
      Nil
  }

}
