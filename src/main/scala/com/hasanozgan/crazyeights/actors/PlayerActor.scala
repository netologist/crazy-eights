package com.hasanozgan.crazyeights.actors

import akka.actor.{Props, Actor}
import com.hasanozgan.crazyeights.messages._
import com.hasanozgan.crazyeights.models.{Rank, Suit, Card, Player}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.io.StdIn._

/**
 * Created by hasan.ozgan on 8/11/2014.
 */
class PlayerActor extends Actor {
  val remote = context.actorSelection("akka.tcp://CrazyEightsSystem@127.0.0.1:2552/user/c8sServer")
  val numberPattern = """([0-9]{1,2})""".r
  var currentPlayer = 1
  var player:Player = Player(0)
  var discardedCard:Card = Card(Suit.CLUB, Rank.ACE)

  override def receive: Receive = {
    case JoinGame =>
      remote ! JoinGame

    case ExitGame =>
      println("Goodbye")
      context.system.shutdown()

    case PlayerJoined(player) =>
      this.player = player
      self ! ShowPrompt

    case ShowPrompt =>
      if (player.no > 0) {
        Future {
          readLine(s"\33[0;32mPlayer #${player.no}\33[0m> ")
        } onSuccess {
          case line => self ! Command(line.trim)
        }
      }

    case Command(cmd) =>
      cmd match {
        case numberPattern(order) =>
          self ! DiscardCard(player, order.toInt)

        case "d" | "draw" =>
          self ! DrawCard(player)

        case "s" | "status" =>
          remote ! ShowGameStatus(player)

        case "h" | "help" =>
          self ! Help

        case "x" | "exit" =>
          remote ! LeaveGame(player)
          self ! ExitGame

        case "" =>
          self ! ShowPrompt

        case _ =>
          self ! ShowMessage(s"not a valid command '${cmd}'")
      }

    case DiscardCard(player, order) =>
      if (myTurn) remote ! DiscardCard(player, order)
      else self ! ShowMessage(s"Please wait Player #${currentPlayer}")

    case DrawCard(player) =>
      if (myTurn) remote ! DrawCard(player)
      else self ! ShowMessage(s"Please wait Player #${currentPlayer}")

    case CurrentPlayer(no) =>
      currentPlayer = no
      if (myTurn) self ! Notification(s"You turn")
      else self ! Notification(s"Game turn to Player #${currentPlayer}")

    case CrazyEightPrompt(card) =>
      println(s"choose new suit for ${card}:")
      println(Suit.values.zipWithIndex.map(x=> "\t%2d - %s".format(x._2+1, x._1)).mkString("\n"))
      val x = readLine("suit> ")
      x match {
        case "1" => remote ! DiscardCrazyEightCard(player, card, Suit.SPADE)
        case "2" => remote ! DiscardCrazyEightCard(player, card, Suit.CLUB)
        case "3" => remote ! DiscardCrazyEightCard(player, card, Suit.HEART)
        case "4" => remote ! DiscardCrazyEightCard(player, card, Suit.DIAMOND)
        case _ => self ! ShowPrompt
      }

    case Help =>
      println("")
      println("Crazy Eights Commands")
      println("---------------------")
      println("h, help    help description")
      println("1..99      discard card operation (choose task order)")
      println("s, status  get game status")
      println("d, draw    draw card")
      println("")
      self ! ShowPrompt

    case GameStatus(handPile:List[Card], discardPile:Card, otherHandPileCounts:Map[Player, Int], stockPileCount: Int) =>
      println("")
      println("Game Stats")
      println("----------")
      println(s"DiscardPile: \t ${discardPile.toString}")
      println(s"StockPile: \t ${Console.BOLD}${Console.MAGENTA}${stockPileCount} cards${Console.RESET}")
      println(s"OtherHandPiles: \n${otherHandPileCounts.map( x=> "\t\t Player #%d has %d cards".format(x._1.no, x._2) ).mkString("\n")}")
      println(s"YourHandPile: \n${handPile.zipWithIndex.map( x=> "\t\t%2d - %s".format(x._2+1, x._1) ).mkString("\n")}")
      println("")
      self ! ShowPrompt

    case GameFinished(won:Player) =>
      if (won.no == currentPlayer) self ! Notification(s"You won")
      else self ! Notification(s"Player #${currentPlayer} won")
      self ! ExitGame

    case ShowMessage(msg) =>
      println(msg)
      self ! ShowPrompt

    case Notification(msg) =>
      println(s"\nNotification: ${msg}")
  }

  def myTurn = player.no == currentPlayer
}
