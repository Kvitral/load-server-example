package ru.kvitral.models

import io.circe.Encoder
import io.circe.generic.semiauto._

case class Pong(ticket: Int)


object Pong {
  implicit val encoder: Encoder[Pong] = deriveEncoder
}
