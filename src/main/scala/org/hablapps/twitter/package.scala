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

import org.hablapps.{ updatable, speech }
import org.hablapps.serializer.{ serializeMe }
import updatable._

object `package` {

  trait State extends speech.System
    with Twitter.State
    with Guest.State
    with Statistics.State
    with account.Account.State
    with account.Tweeter.State
    with account.Follower.State
    with account.Tweet.State
    with account.list.TwitterList.State
    with account.list.Listed.State
    with account.list.ListFollower.State
  
  trait Program extends speech.System
    with State
    with Twitter.Rules
    with Guest.Rules
    with account.Account.Rules
    with account.Tweeter.Rules
    with account.Tweet.Rules
    with speech.serializer.SerializableComponent{ system =>
      serializeMe[system.type]
    
    
    println("Welcome to the Twitter platform!")
  }

  trait PlainSystem extends speech.PlainSystem with Program
}
