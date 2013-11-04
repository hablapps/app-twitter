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

import org.hablapps.{ updatable, speech, twitter }
import updatable._

import language.reflectiveCalls

object Follower {

  trait State { self: speech.System 
    with twitter.State =>

    /** Does show interest in the updates done by certain Tweeter.
      *
      * This kind of agent lives in an account external to the player's one. A
      * user follows a tweeter when he is interested in being notified about
      * the new updates (tweets, retweets...) made by him. Besides, by following
      * somebody the user is able to receive Direct Messages from him.
      */
    trait Follower extends Agent {
      type This = Follower

      /** No substatus is suitable */
      type Substatus = Nothing

      /** This agent lives in the following account */
      type Context = Account

      /** The player of this role is always a Tweeter */
      type Player = Tweeter
      type PlayerCol[x] = Traversable[x]

      /** This player does not play any role. */
      type Role = Nothing
      type RoleCol[x] = Traversable[x]

      /** This agent is responsible for Unfollow the account. */
      type Perform = SocialAction
      type PerformCol[x] = Traversable[x]

      def account = context.head
      def tweeter = player.head
    }
	       
    implicit val Follower = builder[Follower]

    /** Does create a new follower in the chosen account.
      *
      * This action is an extension of a Join, and lets the user to start
      * following a tweeter, by creating a Follower role in his account.
      */
    trait Follow extends Join {
      type This = Follow

      /** No substatus is suitable */
      type Substatus = Nothing

      /** This Speech Act is said in the Account of the following Tweeter. */
      type Context = Account

      /** This can only be said by a Tweeter. */
      type Performer = Tweeter

      type Addressee = Nothing
      type New = Follower

		val _new: Option[Updatable[New]] = Some(Follower())

      def account = context.head
      def tweeter = performer.head
      
      def NewE = implicitly[Evidence[New]]
      def BuilderE: Builder[New] = Follower

      /** This action is empowered if the performer does not appear in the
        * account's blocked list.
		  */
      override def empowered(implicit state: State) = 
		  !account.blocked.contains(tweeter)

      /** If the account is not protected, the permission is granted. In any
       * other case, the Tweeter's approval is needed, so permission can
		 * not be determined by the system itself.
		 */
      override def permitted(implicit state: State) = 
		  if (!account.isPrivate)
			 Some(true)
		  else
			 None
    }

    implicit val Follow = builder[Follow]

    /** Does leave the follower from the account.
      *
      * This action is done when the user is not interested in following this
      * account any more (for instance: annoying tweeters, spammers...).
      */
    trait Unfollow extends Leave {
      type This = Unfollow

      /** This is said in the context of the account that the user is going to
        * abandon. */ 
      type Context = Account

      /** No substatus is available. */
      type Substatus = Nothing

      /** This is said by the Follower who aims to abandon. */
      type Performer = Tweeter

      type Addressee = Nothing

      type Old = Follower
    }

    implicit val Unfollow = builder[Unfollow]

    /** Does approve a Follow request.
      *
      * This approval is needed when the Tweeter has decided to protect his
      * account from public domain and decides to allow a Follow request.
      */
    trait AllowFollowing extends Allow {
      type This = AllowFollowing

      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This action is said in the targetted account. */
      type Context = Account

      /** This action is said by the Tweeter. */ 
      type Performer = Tweeter

      type Addressee = Nothing

      /** This allows a Follow request. */
      type Action = Follow

      def follow = action.head
      def user = performer.head

      /** The only user that is empowered to approve a Follow request is the
        * owner of the target account.
	*/
      override def empowered(implicit state: State) = 
	follow.account.user == user
    }
	       
    implicit val AllowFollowing = builder[AllowFollowing]
	       
    /** Does prohibit a Follow request.
      *
      * This prohibition is needed when the Tweeter has decided to protect his
      * account from public domain and decides to deny a Follow request.
      */
    trait ForbidFollowing extends Forbid {
      type This = ForbidFollowing

      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This action is said in the targetted account. */
      type Context = Account

      /** This action is said by the Tweeter. */
      type Performer = Tweeter

      type Addressee = Nothing

      /** This forbids a Follow request. */
      type Action = Follow

      def follow = action.head
      def user = performer.head

      /** The only user that is empowered to forbid a Follow request is the
        * owner of the target account.
        */      
      override def empowered(implicit state: State) = 
	follow.account.user == user
    }
	       
    implicit val ForbidFollowing = builder[ForbidFollowing]	       
  }

  trait Actions { self: speech.System 
    with twitter.State =>

		case class NotifyFollowers(account: $[Account], event: Event) extends DefinedAction( 
		  For(account.followers){
          case follower => Notify(follower, event)
		  }
      )

  }

}
