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

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.hablapps.updatable._
import org.hablapps.react
import org.hablapps.speech
import org.hablapps.twitter
import org.hablapps.twitter._
import org.hablapps.twitter.account._

import scala.language.reflectiveCalls

@RunWith(classOf[JUnitRunner])
class RegisterScenarioTest extends FunSpec
    with ShouldMatchers
    with BeforeAndAfter {

  describe("Register Scenario") {

    it("should run smoothly") {
      
      object System extends speech.PlainSystem
        with twitter.Program

      import System._

		turn_on_log = true

      val Output(myTwitter, hablappsAccount, hablapps) = reset(for {
      	myTwitter <- Initiate(Twitter())
	hablappsAccount <- Initiate2(
	  (Account().name += "hablapps").biography += "Programming the Information Society", 
	  myTwitter)
	hablapps <- Play2(Tweeter(), hablappsAccount)
      } yield (myTwitter, hablappsAccount, hablapps))

      // Dolly - I heard about Twitter, let's try it out!
      val NewEntities(guest1: $[Guest]@unchecked) =
      	attempt(Play2(Guest(), myTwitter))

      // Dolly - I will gossip Habla Computing...
      attempt(See(
      	guest1, 
      	myTwitter, 
      	GenObservation(_query = Get(hablapps))))

      // Dolly - Twitter rules! I will sign in.
      val NewEntities(_, dollyAccount: $[Account]@unchecked, dolly: $[Tweeter]@unchecked) =
        attempt(Say(
	  guest1, 
	  myTwitter, 
	  SetUpAccount(
	    __new = Some((Account().name += "dolly").biography += "Clon Model"))))

      // Narrator - Some weeks later, Dolly was cloned.

      // Dolly clon - a friend of mine told me about Twitter, let's visit it...
      val NewEntities(guest2: $[Guest]@unchecked) =
      	attempt(Play2(Guest(), myTwitter))

      /* Narrator - They both share hobbies, so she also decided to register an 
       * account.
       */
      attempt(Say(
      	guest2,
      	myTwitter,
      	SetUpAccount(__new = Some(Account().name += "dolly")))) should be (None)

      /* Dolly Clon - Oh dear! There is already a 'dolly' registered. I'll
       * submit a new cool name.
       * Narrator - But Dolly Clon was not original at all...
       */
      val NewEntities(_, clonAccount: $[Account]@unchecked, clon: $[Tweeter]@unchecked) =
        attempt(Say(
      	  guest2,
      	  myTwitter,
      	  SetUpAccount(__new = Some(Account().name += "dolly2"))))
      
      /* Narrator - Dolly was quite curious, so she decided to see who was that
       * @dolly who stole her name, and therefore she discovered her origins.
       * Dolly Clon - Oh my God, I am a clon!
       */
      attempt(See(
      	clon, 
      	dollyAccount, 
      	GenObservation(_query = Get(dolly))))

      /* Narrator - But Dolly could not handle to be a clon, so she decided to
       * leave the application.
       */
      attempt(Say(
	clon,
	clonAccount,
	LeaveTweeter()))

      val obtained = getState()

      reset(for {
      	myTwitter <- Initiate(Twitter())
	hablappsAccount <- Initiate2(
	  (Account()
	    .name += "hablapps")
            .biography += "Programming the Information Society", 
	  myTwitter)
	hablapps <- Play2(Tweeter(), hablappsAccount)
	guest1 <- Play2(Guest(), myTwitter)
	obser1 <- See(
	  guest1, 
	  myTwitter, 
	  GenObservation(_query = Get(hablapps)))
	_ <- Done(obser1, PERFORMED)
	setup1 <- Say(
	  guest1, 
	  myTwitter, 
	  SetUpAccount(
	    __new = Some(
	      (Account().name += "dolly").biography += "Clon Model")))
	_ <- Done(setup1, PERFORMED)
	_ <- Abandon(guest1)
	dollyAccount <- Initiate2(
	  (Account().name += "dolly").biography += "Clon Model", 
	  myTwitter)
	dolly <- Play2(Tweeter(), dollyAccount)
	guest2 <- Play2(Guest(), myTwitter)
	setup2 <- Say(
	  guest2, 
	  myTwitter, 
	  SetUpAccount(__new = Some(Account().name += "dolly2")))
	_ <- Done(setup2, PERFORMED)
	_ <- Abandon(guest2)
	clonAccount <- Initiate2(Account().name += "dolly2", myTwitter)
	clon <- Play2(Tweeter(), clonAccount)
	_ <- Abandon(clon)
	_ <- Finish(clonAccount)
      } yield ())

      obtained should be(getState())
    }
  }
}
