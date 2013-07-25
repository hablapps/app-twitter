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

import org.hablapps.{updatable,speech,twitter}
import updatable._

import language.reflectiveCalls

object Guest {

  trait State { self: speech.System 
    with twitter.State =>

    trait Guest extends Agent {
      type This = Guest
      type Context = Twitter
      type Perform = SocialAction

      def twitter = context.get
    }

    implicit val Guest = builder[Guest]
  }

  trait Rules { self: speech.Program 
    with account.Account.State
    with State =>

    when {
      case Performed(setUp: SetUpAccount) =>
        Abandon(setUp.performer.get)
    }
  }
}
