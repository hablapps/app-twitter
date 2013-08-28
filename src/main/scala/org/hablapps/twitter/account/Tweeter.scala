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
import react.UnauthorizedError

import language.reflectiveCalls

object Tweeter {

  trait State { self: speech.System 
    with twitter.State =>

    trait Tweeter extends Agent {
      type This = Tweeter
      type Context = Account
      type Role = Agent
      type RoleCol[x] = List[x]
      type Perform = SocialAction

      def account = context.head

      def following = alias[Follower, Tweeter](role)
    }

    implicit val Tweeter = builder[Tweeter]

    trait LeaveTweeter extends Leave {
      type This = LeaveTweeter
      type Context = Account
      type Performer = Tweeter
      type Addressee = Follower
    }

    implicit val LeaveTweeter = builder[LeaveTweeter]
  }

  trait Rules { self: speech.Program with State with Account.State with Follower.State =>

    authorised {
      case Play2(agent, _) if agent.isA[Tweeter] => 
        Some(UnauthorizedError("Tweeter already registered!"))
    }

    when {
      case Performed(setUp: SetUpAccount) => 
        Play2(Tweeter(_name=Some("user")), setUp._new_entity.get)
    }

    when {
      case e @ New(_, follower: Follower) =>
        Notify(follower.account.user, e)
    }
  }
}
