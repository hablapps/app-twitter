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

    /** The personal space for the Tweeter and his followers. */
    trait Account extends Interaction {
      type This = Account
      
      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This has the top twitter interaction as context. */
      type Context = Twitter
      type ContextCol[x] = Option[x]

      /** Here you can find one Tweeter and his followers (if any). */
      type Member = Agent@Union[Tweeter,Follower]
      type MemberCol[x] = List[x]

      /** No resource is suitable. */
      type Environment = Nothing
      type EnvironmentCol[x] = Traversable[x]

      /** Many kind of social actions can be said here.
        *
		  * Please, visit Guest, Tweeter and Follower for further info on this
		  * field.
		  */
      type Action = SocialAction
      type ActionCol[x] = Traversable[x]

      /** This is a tree leaf interaction. */
      type Subinteraction = TwitterList
      type SubinteractionCol[x] = Traversable[x]

      /** A small description of this account. */
     val biography: Option[String]

      /** The lsit of blocked Tweeters. */
     val blocked: Set[$[Tweeter]]

      /** Are this account's tweets protected from public observation. */
      val isPrivate: Boolean

      /* Name alias: for instance @hablapps. */
      def username = name.get
      
      /** Context-head alias: the app top interaction. */
      def twitter = context.head

      /** Member-head alias: the tweeter governing this account. */
      def user = member.alias[Tweeter].head 

      /** Member alias: followers of this account. */
      def followers = member.alias[Follower]

      /** Action alias: tweets said by the Tweeter. */
      def tweets = action.alias[Tweet]
    }
				  
    implicit val Account = builder[Account]

    /** Creates a new account.
      *
      * This is the social action that allow guests to create a new account for
      * them.
      */
    trait SetUpAccount extends SetUp {
      type This = SetUpAccount

      /** No substatus is suitable. */
      type Substatus = Nothing

      /** This action must be said in the top twitter interaction. */
      type Context = Twitter

      /** This action can only be said by a guest. */
      type Performer = Guest

      type Addressee = Nothing

      type New = Account

      def account = _new.head
      def twitter = context.head

      def NewE: Evidence[Account] = implicitly[Evidence[Account]]
      def BuilderE: Builder[Account] = Account

      /** If there is not another account with the same name. */
      override def empowered(implicit state: State) = 
		  !twitter.accounts.exists(_.name == account.name)
    }

    implicit val SetUpAccount = builder[SetUpAccount]
  }

  trait Rules { self: speech.Program
      with State 
      with twitter.Guest.State
      with Account.State
      with Tweeter.State => 

    /** Declarations can be done by the account's owner. */
    declarer[Tweeter].of[Account](Account._blocked)
      .empowered {
        case (tweeter, account, blocked) => implicit state => 
			 account.user == tweeter
      }
      .permitted {
        case (agent, entity, value) => implicit state => 
			 Some(true)
      }
      
    declarer[Tweeter].of[Account](Account._biography)
      .empowered {
        case (tweeter, account, blocked) => implicit state => 
			 account.user == tweeter
      }
      .permitted {
        case (agent, entity, value) => implicit state => 
			 Some(true)
      }

    /** The 'name' and 'isPrivate' attributes are always granted. */
    observer[Tweeter].of[Account]
      .empowered_atts(Account._name, Account._isPrivate)
      .empowered { 
        case (performer, EntityQuery(account)) => implicit state => 
			 !account.isPrivate || account.followers.exists(_.tweeter == performer)
      }

    /** An account must be finished when the Tweeter decides to leave it. */
    when {
      case Deleted(_, tweeter: Tweeter) => {
        Finish(tweeter.account)
      }
    }
  }
}
