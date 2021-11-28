package reader

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import org.scalatest.{FunSpecLike, Matchers}
import reader.actor.AkkademyDb
import reader.message.SetRequest

import scala.concurrent.duration.DurationInt

/**
 * 直接对new actor，直接对actor发送消息，对比actor属性是否与预期一致
 */
class AkkademyDbSpec extends FunSpecLike with Matchers{

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)

  describe("akkademyDb"){
    describe("given SetRequest"){
      it("should place key/value into map"){
        val actorRef = TestActorRef(new AkkademyDb) // 创建TestActorRef，获取引用地址(发送地址)
        val (key,value) = ("key","value")
        actorRef ! SetRequest(key,value) // 发送消息
        val akkademyDb = actorRef.underlyingActor // 获取底层真实actor
        akkademyDb.map.get(key) should equal(Some(value)) // 检查是否一致(不一致则抛出异常)
      }
    }
  }

}
