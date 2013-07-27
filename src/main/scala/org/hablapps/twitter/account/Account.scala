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

import org.hablapps.{ updatable, react, speech, twitter}
import updatable._

import language.reflectiveCalls

object Account {

  trait State { self: speech.Program
    with twitter.State =>

    trait Account extends Interaction {
      type This = Account
      type Context = Twitter
      type ContextCol[x] = Option[x]
      type Member = Agent
      type MemberCol[x] = List[x]
      type Action = SocialAction
      type Subinteraction = TwitterList

      val biography: Option[String]
      val blocked: Set[$[Tweeter]]
      val isPrivate: Boolean

      def username = name.get

      def twitter = context.head

      def user = alias[Tweeter, Account](member).head

      def followers = alias[Follower, Account](member)

      def tweetes = alias[Tweet, Account](action)
    }
				  
    implicit val Account = builder[Account]

    trait SetUpAccount extends SetUp {
      type This = SetUpAccount
      type Context = Twitter
      type Performer = Guest
      type Addressee = Nothing
      type New = Account

      def account = _new.head

      def twitter = context.head

      empowered_if{
		    !twitter.accounts.exists(_.name == account.name)
      }
    }

    implicit val SetUpAccount = builder[SetUpAccount]
  }

  trait Rules { self: speech.Program
      with State 
      with twitter.Guest.State
      with Account.State
      with Tweeter.State => 

    declarer[Tweeter].of[Account](Account._blocked)
      .empowered {
        case (tweeter, account, blocked) => account.user == tweeter
      }
      .permitted {
        case (agent, entity, value) => Some(true)
      }
      
    declarer[Tweeter].of[Account](Account._biography)
      .empowered {
        case (tweeter, account, blocked) => account.user == tweeter
      }
      .permitted {
        case (agent, entity, value) => Some(true)
      }

    observer[Tweeter].of[Account]
      .empowered_atts(Account._name, Account._isPrivate)
      .empowered { 
        case (performer, EntityQuery(account)) =>
			   !account.isPrivate || account.followers.exists(_.tweeter == performer)
      }

    when {
      case Deleted(_, tweeter: Tweeter) => {
        Finish(tweeter.account)
      }
    }
  }
}
