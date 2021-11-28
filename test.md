# akka 单元测试

> 直接对new actor，直接对actor发送消息，对比actor属性是否与预期一致
```scala
package reader

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import org.scalatest.{FunSpecLike, Matchers}
import reader.actor.AkkademyDb
import reader.message.SetRequest

import scala.concurrent.duration.DurationInt
/**
 * 通过消息控制存数据，检查存储内容是否正确
 * 直接对比内容，actor没有返回值
 */
class AkkademyDbSpec extends FunSpecLike with Matchers{

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)

  describe("akkademyDb"){
    describe("given SetRequest"){
      it("should place key/value into map"){
        val actorRef = TestActorRef(new AkkademyDb) // 直接创建TestActorRef，获取引用地址(发送地址)
        val (key,value) = ("key","value")
        actorRef ! SetRequest(key,value) // 发送消息
        val akkademyDb = actorRef.underlyingActor // 获取底层真实actor
        akkademyDb.map.get(key) should equal(Some(value)) // 检查是否一致(不一致则抛出异常)
      }
    }
  }

}
```
> 非直接创建actor，借助TestProps对actor发送消息，且接收actor对sender的返回值内容
```scala
package chapter02

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import akka.util.Timeout
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.duration.DurationInt

class PingPongActorTest extends FunSpecLike with Matchers{

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)

  describe("ping-pong"){
    describe("receive Ping send back Pong or throw expection"){
      val testProbe = new TestProbe(system) // 创建观察者
      val pingActor = system.actorOf({Props(new PingPongActor)}) // 创建Actor对象
      val (in,out) = ("Ping","Pong") // 输入、输出
      testProbe.send(pingActor,in) // 观察者准备往actor 发送消息，并接受actor返回值
      testProbe.expectMsg(out) // 检查消息内容
    }
  }

}
```