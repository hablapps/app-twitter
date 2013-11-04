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

    trait Follower extends Agent {
      type This = Follower
      type Context = Account
      type Player = Tweeter
      type Perform = SocialAction

      def account = context.head

      def tweeter = player.head
    }
         
    implicit val Follower = builder[Follower]

    trait Follow extends Join {
      type This = Follow
      type Context = Account
      type Performer = Tweeter
      type New = Follower

      val _new: Option[Updatable[New]] = Some(Follower())

      def account = context.head
      def tweeter = performer.head

      empowered_if{ 
		    !account.blocked.contains(tweeter) && 
        account != tweeter.account
      }

      permitted_if{
        !account.isPrivate
      }
    }

    implicit val Follow = builder[Follow]

    trait Unfollow extends Leave {
      type This = Unfollow
      type Context = Account
      type Performer = Tweeter
      type Old = Follower
    }

    implicit val Unfollow = builder[Unfollow]

    trait AllowFollowing extends Allow {
      type This = AllowFollowing
      type Context = Account
      type Performer = Tweeter
      type Action = Follow

      def follow = action.head
      def user = performer.head

      empowered_if{
        follow.account.user == user
      }
    }
         
    implicit val AllowFollowing = builder[AllowFollowing]
         
    trait ForbidFollowing extends Forbid {
      type This = ForbidFollowing
      type Context = Account
      type Performer = Tweeter
      type Action = Follow

      def follow = action.head
      def user = performer.head
      
      empowered_if{
        follow.account.user == user
      }
    }
         
    implicit val ForbidFollowing = builder[ForbidFollowing]        
  }

  trait Actions { self: speech.System 
    with twitter.State =>

    case class NotifyFollowers(account: $[Account], event: Event) extends DefinedAction( 
      For(account.followers) {
        case follower => Notify(follower, event)
      })
  }
}
