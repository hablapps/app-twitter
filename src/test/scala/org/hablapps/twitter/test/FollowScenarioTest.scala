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

package org.hablapps.twitter.test

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.hablapps.updatable._
import org.hablapps.react
import org.hablapps.speech
import speech._
import org.hablapps.twitter
import org.hablapps.twitter._
import org.hablapps.twitter.account._

import scala.language.reflectiveCalls

class FollowScenarioTest(sys: speech.System with twitter.Program)
  extends FunSuite
    with ShouldMatchers
    with BeforeAndAfter {

  import sys._

  test("Follow Scenario") {

    val Output(
        sunnydale, 
        willowAccount, 
        willow, 
        spikeAccount, 
        spike, 
        buffyAccount, 
        buffy) = reset(for {
      sunnydale <- Initiate(Twitter())
      willowAccount <- Initiate2(
        (Account()
          .name += "willow")
          .biography += "A good (most of time) witch",
        sunnydale)
      willow <- Play2(Tweeter(), willowAccount)
      spikeAccount <- Initiate2(
        (Account()
          .name += "spike")
          .biography += "Vampire without soul",
        sunnydale)
      spike <- Play2(Tweeter(), spikeAccount)
      buffyAccount <- Initiate2(
        (((Account()
          .name += "buffy")
          .biography += "The Vampire Slayer")
          .blocked += spike)
          .isPrivate := true, 
        sunnydale)
      buffy <- Play2(Tweeter(), buffyAccount)
    } yield (
      sunnydale,
      willowAccount,
      willow,
      spikeAccount,
      spike,
      buffyAccount,
      buffy))

    /* Willow requests to follow Buffy, but this account is protected, so
     * she must wait for Buffy to approve (or deny) the action.
     */
    val NewEntities(willowFollow: $[Follow] @unchecked) =
      attempt(Say(willow, buffyAccount, Follow()))

    /* TODO: I suppose that the Willow's desire to follow Buffy should be
     * notified to Buffy herself. Using 'willowFollow' is a kind of cheating.
     */

    /* Undoubtely, Buffy approves her best friend Willow to become her
     * follower.
     */
    attempt(Say(buffy, buffyAccount, AllowFollowing().action += willowFollow))

    /* As Willow did, Spike requests to follow Buffy. However, Buffy blocked
     * him. As a result, Spike is not empowered to follow her, and no action
     * is created.
     */
    attempt(Say(
      spike, 
      buffyAccount, 
      Follow())).toOption should be(None)

    /* One day, Spike got his soul again, and he decided to update his
     * biography.
     */
    attempt(Say(
      spike,
      spikeAccount,
      DeclareLet(
        _entity = Some(spikeAccount),
        _attribute = "biography",
        _value = "Vampire WITH soul",
        _mode = Some(true))))

    /* Due to the fact that Spike was a nice guy again, Buffy decided to
     * remove him from her list of blocked tweeters.
     */
    attempt(Say(
      buffy,
      buffyAccount,
      DeclareLet(
        _entity = Some(buffyAccount),
        _attribute = "blocked",
        _value = spike,
        _mode = Some(false))))

    /* Spike was aware of Buffy knowing about his brand new soul, so maybe she
     * will change her mind. And he was right because a new action was created,
     * so he is empowered to follow her. Since Buffy's account is private, he
     * still need to be approved in order to follow her.
     */
     
    val NewEntities(spikeFollow: $[Follow]@unchecked) =
      attempt(Say(spike, buffyAccount, Follow()))

    /* With his new soul, Spike is going bananas. Therefore, he decides to
     * disappear for a while. He does not want to follow Buffy, so he
     * retracts his follow request before leaving.
     */
    attempt(Retract(spikeFollow))

    /* Meanwhile, Willow has become a black witch and she hates Buffy. So, the
     * very least she can do is to unfollow her.
     */
    attempt(Say(willow.following.head, buffyAccount, Unfollow()))

    val obtained = getState()

    reset(for {
      sunnydale <- Initiate(Twitter())
      willowAccount <- Initiate2(
        (Account()
          .name += "willow")
          .biography += "A good (most of time) witch",
        sunnydale)
      willow <- Play2(Tweeter(), willowAccount)
      spikeAccount <- Initiate2(
        (Account()
          .name += "spike")
          .biography += "Vampire without soul",
        sunnydale)
      spike <- Play2(Tweeter(), spikeAccount)
      buffyAccount <- Initiate2(
        (((Account()
          .name += "buffy")
          .biography += "The Vampire Slayer")
          .blocked += spike)
          .isPrivate := true,
        sunnydale)
      buffy <- Play2(Tweeter(), buffyAccount)
      willowFollow <- Say(
        willow,
        buffyAccount,
        Follow())
      buffyApprovesWillow <- Say(
        buffy,
        buffyAccount,
        AllowFollowing().action += willowFollow)
      _ <- Done(willowFollow, PERFORMED)
      _ <- Done(buffyApprovesWillow, PERFORMED)
      willowFollower <- Play3(Follower(), willow, buffyAccount) flatMap2 {
        case Events(event @ NewEntity(follower: $[Follower] @unchecked, _), _, _, _) => 
          Notify(buffy, event) map { _ => follower }
      }
      biographyLet <- Say(
        spike,
        spikeAccount,
        DeclareLet(
          _entity = Some(spikeAccount),
          _attribute = "biography",
          _value = "Vampire WITH soul",
          _mode = Some(true)))
      _ <- Let(spikeAccount, "biography", "Vampire WITH soul")
      _ <- Done(biographyLet, PERFORMED)
      blockedLet <- Say(
        buffy,
        buffyAccount,
        DeclareLet(
          _entity = Some(buffyAccount),
          _attribute = "blocked",
          _mode = Some(false),
          _value = "").value := spike)
      _ <- Let(buffyAccount, "blocked", spike, false)
      _ <- Done(blockedLet, PERFORMED)
      spikeFollow <- Say(
        spike,
        buffyAccount,
        Follow())
      _ <- Retract(spikeFollow)
      _ <- Abandon(willowFollower)
    } yield ())

    obtained should be(getState())
  }
}
