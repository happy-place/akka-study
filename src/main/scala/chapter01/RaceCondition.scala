package chapter01

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Actor 只会发送消息，基于消息修改内部属性，避免直接修改属性出现多线程竞态条件

object RaceCondition {

  def main(args: Array[String]): Unit = {
    var i,j = 0
    (0 to 10000).foreach(_ => Future(i=i+1)) // 并行，存在竞态条件
    (0 to 10000).foreach(_ => j=j+1) // 串行
    println(s"i=${i}, j=${j}") // i=9991, j=10001
  }

}
