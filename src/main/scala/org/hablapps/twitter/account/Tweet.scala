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

    trait Tweet extends SpeechAct {
      type This = Tweet
      type Context = Account
      type Performer = Tweeter
      type Addressee = Tweeter

      override val persistent = true

      def account = context.head

      def message = dictum

      override def empowered(implicit state: State) =
        message.length != 0

      override def permitted(implicit state: State) =
        Some(message.length < 141)
    }

    implicit val Tweet = builder[Tweet]

    trait Retweet extends SpeechAct {
      type This = Retweet
      type Context = Account
      type Performer = Tweeter

      def account = context.head

      override val persistent = true
      val originalTweet: Option[$[Tweet]]

      override def empowered(implicit state: State) =
        !originalTweet.get.account.isPrivate
    }

    implicit val Retweet = builder[Retweet]

    trait Reply extends SpeechAct {
      type This = Reply
      type Context = Account
      type Performer = Tweeter
      type Addressee = Tweeter

      override val persistent = true
      val originalTweet: Option[$[Tweet]]

      def tweeter = performer.head

      override def empowered(implicit state: State) = true
    }

    implicit val Reply = builder[Reply]

    trait DM extends SpeechAct {
      type This = DM
      type Context = Account
      type Performer = Tweeter
      type Addressee = Tweeter

      override val persistent = true

      def account = context.head

      def message = dictum

      override def empowered(implicit state: State) =
        performer.get.account.followers.exists { _.tweeter == account.user }
    }

    implicit val DM = builder[DM]
  }

  trait Rules { this: speech.System with State with Account.State with Follower.State =>

    when {
      case e @ Performed(tweet: Tweet) =>
        For(tweet.account.followers) {
          case f => Notify(f, e)
        }
    }

    when {
      case e @ Performed(retweet: Retweet) => {
        For(retweet.account.followers) {
          case f => Notify(f, e)
        }
      }
    }
  }
}
