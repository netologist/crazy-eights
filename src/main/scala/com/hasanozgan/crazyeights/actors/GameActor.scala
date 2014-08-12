package com.hasanozgan.crazyeights.actors

import akka.actor.{ActorRef, Actor}
import com.hasanozgan.crazyeights.messages._
import com.hasanozgan.crazyeights.models.{Rank, Card, Deck, Player}

import scala.collection.mutable

/**
 * Created by hasan.ozgan on 8/11/2014.
 */
class GameActor extends Actor {
  var playerCount = 2
  var currentPlayer = Player(1)
  var players:List[ActorRef] = List.empty
  var playerMustDraw:Option[Player] = None

  /* piles */
  var stock:mutable.Stack[Card] = Deck.create
  var discard:List[Card] = List.empty
  var hands:collection.mutable.Map[Player, List[Card]] = collection.mutable.Map.empty

  /* Game Actions */
  override def receive: Receive = {

    case NewGame(playerCount:Int) =>
      this.playerCount = playerCount
      discard = stock.pop :: discard

    case JoinGame =>
      if (gameTurnoutCompleted) {
        sendMessage("You rejected from game server. Game turnout completed")
        sender ! ExitGame
      }
      else {
        val player = newPlayer
        if (player.no == 1) {
          sender ! CurrentPlayer(player.no)
        }
        self ! DealCards(player)
        self ! SubscribeNotification(player, sender)
        sender ! PlayerJoined(player)
      }

    case DealCards(player) =>
      hands += player -> (1 to 8).map( x => stock.pop).toList

    case ShowGameStatus(player:Player) =>
      checkGameStatus(player) match {
        case Some(msg) => sendMessage(msg)
        case None => sender ! GameStatus(hands.getOrElse(player, List.empty), lastDiscardCard, getOtherHandPiles(player), stock.size)
      }

    case DrawCard(player) =>
      checkDrawRules(player) match {
        case Some(msg) => sendMessage(msg)
        case None =>
          val newCard = stock.pop
          hands.update(player, hands.getOrElse(player, List.empty) :+ newCard)
          playerMustDraw = None
          self ! CheckGameResult
          sendMessage(s"your new card is ${newCard}")
      }

    case DiscardCard(player, order) =>
      checkDiscardRules(player, order) match {
        case Left(msg) => sendMessage(msg)
        case Right(card) if Rank.EIGHT.equals(card.rank) =>
          sender ! CrazyEightPrompt(card)

        case Right(card) if Rank.JOKER.equals(card.rank) =>
          self ! NextPlayerMustDraw
          self ! DiscardFromMyHandPile(player, card)
          self ! AddCardToDiscardPile(player, card)

        case Right(card) =>
          self ! DiscardFromMyHandPile(player, card)
          self ! AddCardToDiscardPile(player, card)
      }

    case DiscardCrazyEightCard(player, oldCard, suit) =>
      val newCard = Card(suit, Rank.EIGHT)
      if (!oldCard.equals(newCard))
        sendNotification(s"Player #${player.no} card suit changed from ${s"${oldCard} to ${newCard}"}")

      self ! DiscardFromMyHandPile(player, oldCard)
      self ! AddCardToDiscardPile(player, newCard)

    case AddCardToDiscardPile(player, card) =>
      discard = card :: discard
      sendNotification(s"Player #${player.no} discard ${card}")
      self ! CheckGameResult
      sendMessage(ShowPrompt, player)

    case DiscardFromMyHandPile(player, card) =>
      hands.update(player, getHandPile(player) diff List(card))

    case NextPlayerMustDraw =>
      playerMustDraw = Some(getNextPlayer)

    case TurnPlayer =>
      currentPlayer = getNextPlayer
      sendNotification(CurrentPlayer(currentPlayer.no))

    case SubscribeNotification(player, playerActorRef) =>
      context.system.eventStream.subscribe(playerActorRef, classOf[Message])
      sendNotification(s"Player #${player.no} joined")

      if (gameTurnoutCompleted) {
        sendNotification("All players joined")
      }

    case CheckGameResult =>
      val leastHands = hands.minBy( x => x._2.length)
      if (leastHands._2.isEmpty || stock.isEmpty) {
        sendNotification(GameFinished(leastHands._1))
      }
      else self ! TurnPlayer

    case LeaveGame(player) =>
      sendNotification(s"Player #${player.no} leaved game!")
      sendNotification(ExitGame)
  }


  private def newPlayer: Player = {
    players = players :+ sender
    Player(players.size)
  }

  private def getOtherHandPiles(player:Player): Map[Player, Int] = {
    val k = for {
      (x,y) <- hands
      if !x.equals(player)
    } yield x -> y.length
    k.toMap[Player, Int]
  }

  private def sendMessage(msg:String, to: Player) = {
    getPlayerActorRef(to) match {
      case Some(playerActorRef) => playerActorRef ! ShowMessage(msg)
      case None =>
    }
  }

  private def sendMessage(msg:Message, to: Player) = {
    getPlayerActorRef(to) match {
      case Some(playerActorRef) => playerActorRef ! msg
      case None =>
    }
  }

  private def checkGameStatus(player: Player): Option[String] = {
    if (!gameTurnoutCompleted) Some("%d player(s) waiting".format(expectedPlayerCount))
    else None
  }

  private def checkDrawRules(player: Player): Option[String] = {
    if (!gameTurnoutCompleted)
      Some("%d player(s) waiting".format(expectedPlayerCount))
    else if (!player.equals(currentPlayer))
      Some(s"Please wait Player #${currentPlayer}")
    else
      None
  }

  private def checkDiscardRules(player: Player, order: Int): Either[String, Card] = {
    if (!gameTurnoutCompleted)
      Left("%d player(s) waiting".format(expectedPlayerCount))
    else if (!player.equals(currentPlayer))
      Left(s"Please wait Player #${currentPlayer}")
    else {
      getCardFromHandPile(player, order) match {
        case Some(card) =>
          if (Rank.JOKER.equals(card.rank))
            Right(card)
          else if (playerMustDraw.isDefined && currentPlayer.equals(playerMustDraw.get))
            Left(s"you must draw a card")
          else if (card.rank.equals(lastDiscardCard.rank) ||
            card.suit.equals(lastDiscardCard.suit) ||
            Rank.JOKER.equals(lastDiscardCard.rank) ||
            Rank.EIGHT.equals(card.rank))
            Right(card)
          else
            Left("you discard valid card or draw a card")
        case None => Left("your choose is invalid")
      }
    }
  }

  private def getNextPlayer = if (currentPlayer.no >= players.length) Player(1) else Player(currentPlayer.no + 1)

  private def getPlayerActorRef(player:Player) = players.lift(player.no-1)

  private def lastDiscardCard: Card = discard.head

  private def expectedPlayerCount = playerCount - players.length

  private def gameTurnoutCompleted = playerCount == players.length

  private def sendMessage(msg:String) = sender ! ShowMessage(msg)

  private def sendMessage(msg:Message) = sender ! msg

  private def sendNotification(msg: String) = context.system.eventStream.publish(Notification(msg))

  private def sendNotification(msg: Message) = context.system.eventStream.publish(msg)

  private def getCardFromHandPile(player: Player, order: Int) = getHandPile(player).lift(order-1)

  private def getHandPile(player: Player) = hands.getOrElse(player, List.empty)
}
