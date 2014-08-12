package com.hasanozgan.crazyeights.models

/**
 * Created by hasanozgan on 07/08/14.
 */
case class Card(suit:Suit.Value, rank:Rank.Value) {

  override def toString = {
    rank match {
      case Rank.JOKER => s"${Console.BOLD + Console.CYAN}${rank.toString}${Console.RESET}"
      case _ =>
        val color = suit match {
          case Suit.CLUB => Console.BOLD + Console.GREEN
          case Suit.SPADE =>Console.BOLD + Console.BLUE
          case Suit.HEART => Console.BOLD + Console.YELLOW
          case Suit.DIAMOND => Console.BOLD + Console.RED
        }

        s"${color}${suit} ${rank}${Console.RESET}"
    }
  }
}