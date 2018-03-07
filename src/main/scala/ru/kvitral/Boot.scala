package ru.kvitral

import java.lang.management.ManagementFactory

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.pattern.after
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import javax.management.{MBeanServer, ObjectName}
import ru.kvitral.mbeans.{DefaultMBean, Hello, HelloMBean}
import ru.kvitral.services.{ListAdder, PingPong}

import scala.concurrent.Future
import scala.concurrent.duration._


object Boot extends App {


  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val config = ConfigFactory.load
  var beans = scala.collection.mutable.Map.empty[String, DefaultMBean]
  var leakList = List.empty[Array[Int]]
  val properListAdder = new ListAdder(10000000)
  val slowerListAdder = new ListAdder(100000)


  val pingPongRoute =
    pathPrefix("ping") {
      parameter("ticket".as[Int]) { ticket =>
        complete(PingPong.sayPong(ticket))
      } ~ path("corrupted") {
        parameter("ticket_cor".as[Int]) { ticket =>
          complete(PingPong.sayCorruptedPong(ticket))
        }
      }
    }

  val quickSlowRoute =
    path("quick") {
      complete("this is quick")
    } ~
      path("slow") {
        def slowFuture = after(10 second, system.scheduler)(Future.successful("this is slow"))

        complete(slowFuture)
      }

  val helloBeanRoute =
    path("hello") {
      get {
        val str = beans.get("hello").map {
          case x: HelloMBean => x.getName
        }.getOrElse("")
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Say hello to $str</h1>"))
      } ~
        post {
          formFields('name) {
            name =>
              beans.get("hello").foreach {
                case x: HelloMBean => x.setName(name)
              }
              complete("ok")

          }
        }
    }

  val leakRoute = path("leak") {
    get {
      leakList = leakList :+ new Array[Int](leakList.size * 10000 + 1)
      complete("bla")
    }
  }

  val addersRoute = pathPrefix("adder") {
    path("proper") {
      parameter("element".as[Int]) { element =>
        properListAdder.properAddToList(element)
        complete("proper")
      }
    } ~ path("slower") {
      parameter("element".as[Int]) { element =>
        properListAdder.slowerAddToList(element)
        complete("slower")
      }
    }
  }

  val concatedRoute = pingPongRoute ~ quickSlowRoute ~ helloBeanRoute ~ leakRoute ~ addersRoute

  Http().bindAndHandle(concatedRoute, "127.0.0.1", 8080)
  initMbeans


  def initMbeans = {
    val hello = new Hello
    val mbs: MBeanServer = ManagementFactory.getPlatformMBeanServer
    val mBeanName: ObjectName = new ObjectName("ru.fintech:type=Hello")
    mbs.registerMBean(hello, mBeanName)
    beans += "hello" -> hello
  }

}
