package co.technius.chatty.server.service

sealed trait InternalProtocol
object InternalProtocol {
  case class InboundMessage(sender: String, msg: String) extends InternalProtocol
  case class OutboundMessage(msg: String) extends InternalProtocol
  case class Join(name: String) extends InternalProtocol
  case class Leave(name: String) extends InternalProtocol
}
