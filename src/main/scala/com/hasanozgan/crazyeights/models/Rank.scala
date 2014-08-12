package com.hasanozgan.crazyeights.models

/**
 * Created by hasanozgan on 07/08/14.
 */
object Rank extends Enumeration {
  type Rank = Value

  val ACE = Value("ACE")
  val TWO = Value("2")
  val THREE = Value("3")
  val FOUR = Value("4")
  val FIVE = Value("5")
  val SIX = Value("6")
  val SEVEN = Value("7")
  val EIGHT = Value("* 8 *")
  val NINE = Value("9")
  val TEN = Value("10")
  val JACK = Value("JACK")
  val QUEEN = Value("QUEEN")
  val KING = Value("KING")
  val JOKER = Value("JOKER")
}

