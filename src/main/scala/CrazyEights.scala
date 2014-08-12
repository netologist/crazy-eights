import akka.actor.{Props, ActorSystem}
import com.hasanozgan.crazyeights.actors.{PlayerActor, GameActor}
import com.hasanozgan.crazyeights.messages._
import com.typesafe.config.ConfigFactory
import akka.pattern.{ ask, pipe }

/**
 * Created by hasan.ozgan on 8/11/2014.
 */
object CrazyEights {
  val config = ConfigFactory.load()

  def startGame(playerCount:Int) = {
    val system = ActorSystem("CrazyEightsSystem", config.getConfig("server"))
    val serverActor = system.actorOf(Props[GameActor], "c8sServer")
    val clientActor = system.actorOf(Props[PlayerActor], "c8sClient")
    serverActor ! NewGame(playerCount)
    clientActor ! JoinGame
  }

  def joinGame = {
    val system = ActorSystem("CrazyEightsSystem", config.getConfig("client"))
    val clientActor = system.actorOf(Props[PlayerActor], "c8sClient")
    clientActor ! JoinGame
  }
}
