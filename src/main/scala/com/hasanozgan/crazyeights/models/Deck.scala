package com.hasanozgan.crazyeights.models

import scala.util.Random
import scala.collection.mutable.Stack

/**
 * Created by hasanozgan on 07/08/14.
 */
object Deck {
  def create = {
    new Stack[Card].pushAll(Random.shuffle(Suit.values.flatMap(s => Rank.values.map(r => Card(s, r))).toList))
  }
}
