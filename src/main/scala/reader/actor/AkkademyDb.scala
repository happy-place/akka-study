package reader.actor

import akka.actor.Actor
import akka.event.Logging
import reader.message.SetRequest

import scala.collection.mutable

class AkkademyDb extends Actor{
  val map = new mutable.HashMap[String,Object]()
  val log = Logging(context.system,this)

  override def receive: Receive = {
    case SetRequest(key,value) => {
      log.info("receive SetRequest - key: {} value: {}",key,value)
      map.put(key,value)
    }
    case o => log.info("receive unknown message: {}",o)
  }

}
