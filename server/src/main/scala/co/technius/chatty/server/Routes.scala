package co.technius.chatty.server

import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

import service._

class Routes(page: String, service: ChatService) {
  val default: Route =
    encodeResponse {
      path("chat") {
        parameter("name") { name =>
          handleWebSocketMessages(service.flow(name))
        } ~
        complete {
          StatusCodes.BadRequest -> "name parameter required"
        }
      } ~
      pathSingleSlash {
        getFromResource("public/index.html")
      } ~
      getFromResourceDirectory("public")
    }
}
