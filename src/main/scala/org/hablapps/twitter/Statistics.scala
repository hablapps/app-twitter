package org.hablapps.twitter

import org.hablapps.updatable._
import org.hablapps.speech

import language.reflectiveCalls

object Statistics {

  trait State { self: speech.System with account.Tweet.State with Twitter.State =>

    trait Statistics extends Resource{
      type This = Statistics
      type Context = Twitter

      val top_tweet : List[Tweet]
      val trending_topic : List[String]
    }

    implicit val Statistics = builder[Statistics]
  }

}
