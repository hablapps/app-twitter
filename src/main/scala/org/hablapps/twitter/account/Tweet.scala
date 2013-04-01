/*
 * Copyright (c) 2013 Habla Computing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hablapps.twitter.account

import org.hablapps.updatable._
import org.hablapps.speech
import org.hablapps.twitter

import language.reflectiveCalls

object Tweet {

  trait State { self: speech.System 
      with twitter.Twitter.State
      with twitter.Guest.State
      with Account.State
      with Follower.State
      with Tweeter.State =>

    /** Any message with fewer than 140 characters posted to Twitter.
      *
      * This is perhaps the most important item in the application. This message
      * aims to answer the question "What is happening?". It is quite natural to
      * introduce this element in the system as a SpeechAct.
      */
    trait Tweet extends SpeechAct {
      type This = Tweet

      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This message is said in the Tweeter's account. */
      type Context = Account

      /** This can be said by a Tweeter. */
      type Performer = Tweeter

      type Addressee = Tweeter

      /** Do not want to miss a tweet when said. */ 
      override val persistent = true

      /** Context alias to find where the Tweet was posted. */
      def account = context.head

      /** Dictum alias. The Tweet's body. */
      def message = dictum

      /** If the message is not null. */
      override def empowered(implicit state: State) =
	message.length != 0

      /** If the tweet length is valid (< 141). */
      override def permitted(implicit state: State) =
	Some(message.length < 141)
    }

    implicit val Tweet = builder[Tweet]

    /** A Tweet that is re-sent by another Tweeter.
      *
      * If a Tweeter considers a Tweet as interesting, he is able to re-tweet
      * it. This is the main cause of Twitter to be a viral channel.
      */
    trait Retweet extends SpeechAct {
      type This = Retweet
      
      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This lives in the account of the Tweeter who is re-tweeting. */
      type Context = Account

      /** This can only be said by a Tweeter. */
      type Performer = Tweeter

      type Addressee = Nothing
      
      /** Context-head alias: the account where the retweet was performed. */
      def account = context.head

      override val persistent = true
      
      /** ''Original'' tweet we are re-tweeting. */
      val originalTweet: Option[$[Tweet]]

      /** If the account where the tweet was created is not protected.
        *
	* If we allowed this, the protected tweets would become public.
	*/
      override def empowered(implicit state: State) = 
		  !originalTweet.get.account.isPrivate
    }

    implicit val Retweet = builder[Retweet]

    /** A tweet that begins with a mention to another user and is in reply to
      * one of his tweets. 
      *
      * This kind of message is really important to keep track of the
      * conversations.
      */
    trait Reply extends SpeechAct {
      type This = Reply

      /** No substatus is suitable. */
      type Substatus = Nothing

      /** A @reply is said in the account of the tweeter we are replying to. */
      type Context = Account

      /** This can only be said by a Tweeter. */
      type Performer = Tweeter

      type Addressee = Tweeter

      override val persistent = true

      /** The "in reply to" tweet. */
      val originalTweet: Option[$[Tweet]]

      /** Performer-head alias: tweeter who said this. */
      def tweeter = performer.head

      /** If the user we are replying to has not blocked the performer. */
      override def empowered(implicit state: State) =
		  !originalTweet.get.context.head.blocked.exists(_ == tweeter)
    }

    implicit val Reply = builder[Reply]

    /** A personal message sent directly to someone who follows you or sent
      * directly to you from someone you follow.
      */
    trait DM extends SpeechAct {
      type This = DM

      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This is said in the receiver's account. */
      type Context = Account

      /** The sender must be a Tweeter. */
      type Performer = Tweeter

      type Addressee = Tweeter

      override val persistent = true

      /** Context-head alias: account where this was said. */
      def account = context.head

      /** Dictum alias: message body. */
      def message = dictum

      /** If the receiver follows the sender. */
      override def empowered(implicit state: State) =
	performer.get.account.followers.exists { _.tweeter == account.user }
    }

    implicit val DM = builder[DM]
  }

  trait Rules { this: speech.System 
      with State 
      with Account.State 
      with Follower.State =>

    /** A tweet has to be notified to all the performer's followers. */
    when {
      case e @ Performed(tweet:Tweet) => {
		  For(tweet.account.followers) {
          case f => Notify(f, e)
		  }
      }
    }
					
    /** A re-tweet has to be notified to all the performer's followers. */
    when {
      case e @ Performed(retweet: Retweet) => {
        For(retweet.account.followers) {
			 case f => Notify(f, e)
		  }
      }
    }
  }
}
