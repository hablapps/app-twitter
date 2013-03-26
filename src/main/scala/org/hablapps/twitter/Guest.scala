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

package org.hablapps.twitter

import org.hablapps.updatable._
import org.hablapps.speech

import language.reflectiveCalls

object Guest {

  trait State { self: speech.System with Twitter.State =>

    /** A non-registered user who visits Twitter.
      *
      * This is a temporal role that is empowered to sign the user in the
      * application. If the user does not want to do so, this role allows him to
      * be a mere observer of the issues happening around. It worths noticing
      * that in any case this kind of role will be able to observe protected
      * tweets.
      */
    trait Guest extends Agent {
      type This = Guest

      /** This does not hold any substatus. */
      type Substatus = Nothing

      /** This role could only appear on a Twitter. */
      type Context = Twitter

      /** This is a top-role, so no player is suitable. */
      type Player = Nothing
      type PlayerCol[x] = Traversable[x]

      /** This agent does not play any role. */
      type Role = Nothing
      type RoleCol[x] = Option[x]

      /** Performs account setups and observations. */
      type Perform = SocialAction
      type PerformCol[x] = Traversable[x]

      /** Alias to retrieve the context. */
      def twitter = context.get
    }

    implicit val Guest = builder[Guest]
  }

  trait Rules { self: speech.Program 
    with account.Account.State
    with State =>

    /** @abandon when this agent sets up an account */
    when {
      case Performed(setUp: SetUpAccount) => implicit state => 
        Abandon(setUp.performer.get)
    }
  }
}
