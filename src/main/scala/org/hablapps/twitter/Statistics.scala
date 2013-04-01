package org.hablapps.twitter

import org.hablapps.updatable._
import org.hablapps.speech

import language.reflectiveCalls

object Statistics {

  trait State { self: speech.System with account.Tweet.State with Twitter.State =>

    /** 
      * A resource which encapsulates statistical information about the activity of the Twitter network.
      */
    trait Statistics extends Resource{
      type This = Statistics

      /** This does not hold any substatus. */
      type Substatus = Nothing

      /** This is a global resource so it is deployed within the top-level twitter interaction.  */
      type Context = Twitter

      /** This resource is created automatically. */
      type Creator = Nothing

      /** This resource is not owned by any particular agent. */
      type Owner = Nothing
      type OwnerCol[x] = Traversable[x]

      /** Top tweets */
      val top_tweet : List[Tweet]

		/** Trending topics */
		val trending_topic : List[String]
    }

    implicit val Statistics = builder[Statistics]
  }

}
