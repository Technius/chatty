package co.technius.chatty.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

object ChattyServer {
  def main(args: Array[String]): Unit = {
    startServer("localhost", 8080)
  }

  def startServer(host: String, port: Int): Unit = {
    implicit val system = ActorSystem("chatty")
    implicit val materializer = ActorMaterializer()
    implicit val dispatcher = system.dispatcher

    val chatService = new service.ChatService
    val routes = new Routes("index.html", chatService)

    val bindFut = Http().bindAndHandle(routes.default, host, port)

    println(s"Server started at $host:$port")
    if (System.console() != null) {
      io.StdIn.readLine()
      println("Server shutting down")
      bindFut
        .flatMap(_.unbind())
        .onComplete { _ =>
          system.terminate()
        }
    }
  }
}
