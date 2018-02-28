package ru.kvitral

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.Future


object Boot extends App {


  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val config = ConfigFactory.load


  val route =
    pathPrefix("ping") {
      parameter("ticket".as[Int]) { ticket =>
        complete(PingPong.sayPong(ticket))
      } ~ path("corrupted") {
        parameter("ticket_cor".as[Int]) { ticket =>
          complete(PingPong.sayCorruptedPong(ticket))
        }
      }
    } ~
      path("quick") {
        complete("this is quick")
      } ~
      path("slow") {
        def slowFuture = Future {
          Thread.sleep(10000)
          "this is slow"
        }

        complete(slowFuture)
      }

  Http().bindAndHandle(route, "127.0.0.1", 8080)

}
