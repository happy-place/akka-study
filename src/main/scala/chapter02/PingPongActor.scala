package chapter02

import akka.actor.{Actor, Status}

import java.util.concurrent.TimeUnit

/**
 * 接收Ping，返回Pong，其余全部报异常
 */
class PingPongActor extends Actor{

  override def receive: Receive = {
    case "Ping" => {
      TimeUnit.SECONDS.sleep(2)
      sender() ! "Pong"
    } // 返回Pong
    case _ => {
      sender() ! Status.Failure(new Exception("unknown exception"))
    } // 返回failure
  }

}
