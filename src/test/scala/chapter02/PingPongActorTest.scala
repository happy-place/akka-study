package chapter02

import akka.actor.{ActorSystem, Props, Status}
import akka.pattern.ask
import akka.testkit.TestProbe
import akka.util.Timeout
import org.scalatest.{FunSpecLike, Matchers}

import java.util.concurrent.TimeoutException
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
 * 非直接创建actor，借助TestProps对actor发送消息，且接收actor对sender的返回值内容
 */
class PingPongActorTest extends FunSpecLike with Matchers{

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)

  describe("ping-pong"){
    val testProbe = new TestProbe(system) // 创建观察者
    // 任何时候都尽量避免直接创建actor实例，而应该通过actorRef获取actor的引用
    val pingActorRef = system.actorOf({Props(new PingPongActor)}) // 创建Actor对象

    describe("send Ping then receive Pong"){
      val (in,out) = ("Ping","Pong") // 输入、输出
      testProbe.send(pingActorRef,in) // 观察者准备往actor 发送消息，并接受actor返回值
      testProbe.expectMsg(out) // 检查消息内容
    }

    describe("send no Pang then receive failure"){
      val (in,out) = ("Pang",Status.Failure(new Exception("unknown exception"))) // 输入、输出
      testProbe.send(pingActorRef,in) // 观察者准备往actor 发送消息，并接受actor返回值
      testProbe.expectMsgClass(classOf[Status.Failure]) // 返回值类型
    }

    describe("asyc call send Ping then receive Pong after 2 seconds"){

      it("should response with Pong"){
        val (in,out) = ("Ping","Pong")
        //  val future = ask(pingActorRef, in, 1000) // ask 等效于 ? 异步发送消息，tell 等效于 ！，同步发送消息
        val future = pingActorRef ? in // 未传入超时，就使用默认超时timeout
//        val result = Await.result(future.mapTo[String],1 seconds) // 最长等待时间，响应需要2秒，超时异常
        val result = Await.result(future.mapTo[String],3 seconds) // 最长等待时间3.秒，响应需要2秒，正常完成
        assert(result.equals(out))
      }

      it("should receive timeout exception"){
        val (in,out) = ("Ping","Pong")
        // ask 等效于 ? 异步发送消息且能获得一个future返回值，tell 等效于 ！，同步发送消息返回值为void，要获取返回结果需要借助testProbe
        //  val future = ask(pingActorRef, in, 1000)
        val future = pingActorRef ? in // 未传入超时，就使用默认超时timeout
        intercept[TimeoutException]{ // java.util.concurrent.TimeoutException
          Await.result(future.mapTo[String],1 seconds)
        }

      }

      it("should fail receive unknown message"){
        val (in,out) = ("unknown","unknown message")
        val future = pingActorRef ? in
        intercept[Exception]{
          Await.result(future.mapTo[String],3 seconds)
        }
      }
    }


    
  }

}
