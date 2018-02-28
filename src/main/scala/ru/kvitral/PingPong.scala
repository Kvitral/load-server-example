package ru.kvitral

import io.circe.generic.JsonCodec

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object PingPong {
  def sayPong(ticket: Int)(implicit executionContext: ExecutionContext) = Future {
    val rndSleep = Random.nextInt(5)
    Thread.sleep(rndSleep * 1000)
    Pong(ticket)

  }

  def sayCorruptedPong(ticket: Int)(implicit executionContext: ExecutionContext) = Future {
    val rnd = Random.nextInt(10)
    if (rnd > 5)
      sayPong(ticket)
    else
      sayPong(0)
  }
}




