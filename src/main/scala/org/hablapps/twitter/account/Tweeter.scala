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

import org.hablapps.{updatable,react,speech,twitter}
import updatable._

import language.reflectiveCalls

object Tweeter {

  trait State { self: speech.System 
    with twitter.State =>

    /** Posts all kind of tweets.
      *
      * This is the most important agent in the Twitter system. He is able to
      * post normal tweets, mentions, replies and direct messages. Besides he
      * can start a follow request, as well as editing or even closing an
      * account (by leaving it).
      */
    trait Tweeter extends Agent {
      type This = Tweeter

      /** No substatus suitable. */
      type Substatus = Nothing

      /** This lives in an Account. */
      type Context = Account

      /** This is a top-role, so no player is suitable. */
      type Player = Nothing

      /** Tweeters can be Follower in lists and accounts, and be Listed. */
      type Role = Agent@Union3[Follower,Listed,ListFollower]
      type RoleCol[x] = Traversable[x]

      /** This agent can achieve tweets, re-tweets, mentions, replies,
        * direct messages, follow requests and finally a leave, if he aims to
		  * unregister from the application.
        */
      type Perform = SocialAction
      type PerformCol[x] = Traversable[x]

      /** Context alias to get the account. */
      def account = context.head

      /** List of following roles that this agent is playing. */
      def following = role.alias[Follower]
    }

    implicit val Tweeter = builder[Tweeter]

    /** Abandons a Tweeter agent.
      *
      * This action should be performed in an account's context and can only
      * be said by a Tweeter. It expresses his desire to unregister from the
      * application. Leaving an account is an important issue, so some rules
      * will be activated while doing so (notify followers, close account...).
      */
    trait LeaveTweeter extends Leave {
      type This = LeaveTweeter

      /** This leave is said in the account context. */
      type Context = Account

      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This can be said by the Tweeter aiming to abandon the application. */
      type Performer = Tweeter

      /** Followers should be notified about this departure. */
      type Addressee = Follower

      type Old = Tweeter
    }

    implicit val LeaveTweeter = builder[LeaveTweeter]
  }

  trait Rules { self: speech.Program with State with Account.State with Follower.State =>

    /** @play/1 Tweeter agents are top-level roles, but they can't
     * directly be created by components. Instead, components must
     * enter as guests, and then set up an account.
     */
    authorised {
      case Play2(agent, _) if agent.isA[Tweeter] => 
        false
    }

    /** @play/2 Tweeter agents are created automatically when the account is
      * set up.
      */
    when {
      case Performed(setUp: SetUpAccount) => 
        Play2(Tweeter(_name=Some("user")), setUp._new_entity.get)
    }
    
    /** @notify Tweeter agents are automatically notified when they are followed by a new tweeter
      */
    when {
      case e @ New(_, follower: Follower) =>
        Notify(follower.account.user, e)
    }

  }
}
