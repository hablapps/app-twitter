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

object Twitter {

  trait State { this: speech.System
    with twitter.State =>

    trait Twitter extends Interaction {
      type This = Twitter
      type Member = Guest
      type MemberCol[x] = List[x]
      type Environment = Statistics
      type Action = SocialAction
      type Subinteraction = Account

      def accounts = alias[Account, Twitter](subinteraction)

      def guests = member
    }

    implicit val Twitter = builder[Twitter]
  }

  trait Rules { this: speech.System with Twitter.State => 
    authorised {
      case Initiate(interaction) if interaction.isA[Twitter] => { 
		    implicit state => tops.isEmpty
      }
    }
  }
}
