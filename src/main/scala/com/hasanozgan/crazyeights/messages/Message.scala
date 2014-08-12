package com.hasanozgan.crazyeights.messages

import akka.actor.ActorRef
import com.hasanozgan.crazyeights.models.{Suit, Card, Player}

/**
 * Created by hasan.ozgan on 8/11/2014.
 */
sealed trait Message

case object JoinGame extends Message

case object ExitGame extends Message

case object ShowPrompt extends Message

case object TurnPlayer extends Message

case object NextPlayerMustDraw extends Message

case object Help

case object CheckGameResult extends Message

case class CurrentPlayer(playerNo:Int) extends Message

case class NewGame(playerCount:Int) extends Message

case class DrawCard(player:Player) extends Message

case class CrazyEightPrompt(card: Card) extends Message

case class LastDiscardedCard(card: Card) extends Message

case class DiscardCard(player:Player, order:Int) extends Message

case class DiscardFromMyHandPile(player:Player, card:Card) extends Message

case class AddCardToDiscardPile(player:Player, card:Card) extends Message

case class DiscardCrazyEightCard(player:Player, card:Card, newSuit:Suit.Value) extends Message

case class SubscribeNotification(player:Player, playerActorRef:ActorRef) extends Message

case class DealCards(player:Player) extends Message

case class ShowGameStatus(player:Player) extends Message

case class GameStatus(handPile:List[Card], discardPile:Card, otherHandPileCounts:Map[Player, Int], stockPileCount:Int) extends Message

case class LeaveGame(player:Player) extends Message

case class PlayerJoined(player:Player) extends Message

case class GameFinished(won:Player) extends Message

case class Command(line:String) extends Message

case class Notification(msg:String) extends Message

case class ShowMessage(msg:String) extends Message

