package ru.kvitral.services

import akka.actor.ActorSystem
import ru.kvitral.models.Pong

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Random

object PingPong {
  def sayPong(ticket: Int)(implicit system: ActorSystem): Future[Pong] = {
    implicit val executionContext: ExecutionContext = system.dispatcher
    val rndSleep = Random.nextInt(5)
    val promise = Promise[Pong]

    system.scheduler.scheduleOnce(rndSleep seconds) {
      promise.success(Pong(ticket))
    }
    promise.future
  }


  def sayCorruptedPong(ticket: Int)(implicit system: ActorSystem): Future[Pong] = {

    val rnd = Random.nextInt(10)
    if (rnd > 5)
      sayPong(ticket)
    else
      sayPong(0)
  }
}




