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

class TweetScenarioTest(sys: speech.System with twitter.Program) 
  extends FunSuite
    with ShouldMatchers
    with BeforeAndAfter {

  import sys._

  test("Follow Scenario") {

    val Output(
        castilla,
        dulcineaAccount,
        dulcinea,
        quijoteAccount,
        quijote,
        sanchoAccount,
        sancho) = reset(for {
      castilla <- Initiate(Twitter())
      dulcineaAccount <- Initiate2(
        (Account()
          .name += "dulcinea")
          .biography += "Moza del Toboso",
        castilla)
      dulcinea <- Play2(Tweeter(), dulcineaAccount)
      quijoteAccount <- Initiate2(
        (Account()
          .name += "quijote")
          .biography += "El Caballero de la Triste Figura",
        castilla)
      quijote <- Play2(Tweeter(), quijoteAccount)
      sanchoAccount <- Initiate2(
        (Account()
          .name += "sancho")
          .biography += "Fiel Escudero y Amigo",
        castilla)
      sancho <- Play2(Tweeter(), sanchoAccount)
    } yield (
      castilla,
      dulcineaAccount,
      dulcinea,
      quijoteAccount,
      quijote,
      sanchoAccount,
      sancho))

    // @quijote seems to be thinking about somebody
    val NewEntities(tweet: $[Tweet]@unchecked) = attempt(Say(
      quijote,
      quijoteAccount,
      Tweet(_dictum = "Just thinking about her...")))

    // @sancho advises him to write her
    attempt(Say(
      sancho,
      quijoteAccount,
      Reply(
        _dictum = "@quijote then you should write her a private message.",
        _originalTweet = Option(tweet))))

    /* @quijote tries to send @dulcinea a direct message, but the attempt
     * fails, just because @dulcinea is not following him.
     */
    attempt(Say(
      quijote,
      dulcineaAccount,
      DM(_dictum = "Soberana y alta señora: El herido de punta..."))).toOption should be(None)

    // So @quijote tries to talk her into following him.
    attempt(Say(
      quijote,
      quijoteAccount,
      Tweet(
        _dictum = "@dulcinea could you follow me to direct messaging?")))

    // @dulcinea agrees to follow him.
    attempt(Say(
      dulcinea, 
      quijoteAccount, 
      Follow()))

    /* And @quijote sends her (a reduced version of) the letter he wrote, as
     * the body of a direct message. Remember: since @dulcinea follows him,
     * he is able to do so.
     */
    val NewEntities(dm: $[DM]@unchecked) = attempt(Say(
      quijote,
      dulcineaAccount,
      DM(_dictum = "Erm... Soberana y alta señora: El herido de punta...")))

    /* Noticing that @quijote was thinking about her, a proud @dulcinea decides
     * to retweet.
     */
    attempt(Say(
      dulcinea,
      dulcineaAccount,
      Retweet(_originalTweet = Option(tweet))))

    val obtained = getState()

    reset(for {
      castilla <- Initiate(Twitter())
      dulcineaAccount <- Initiate2(
        (Account()
          .name += "dulcinea")
          .biography += "Moza del Toboso",
        castilla)
      dulcinea <- Play2(Tweeter(), dulcineaAccount)
      quijoteAccount <- Initiate2(
        (Account()
          .name += "quijote")
          .biography += "El Caballero de la Triste Figura", 
        castilla)
      quijote <- Play2(Tweeter(), quijoteAccount)
      sanchoAccount <- Initiate2(
        (Account()
          .name += "sancho")
          .biography += "Fiel Escudero y Amigo",
        castilla)
      sancho <- Play2(Tweeter(), sanchoAccount)
      tweet <- Say(
        quijote,
        quijoteAccount,
        Tweet(_dictum = "Just thinking about her..."))
      _ <- Perform(tweet)
      reply <- Say(
        sancho,
        quijoteAccount,
        Reply(
          _dictum = "@quijote then you should write her a private message.",
          _originalTweet = Option(tweet)))
      _ <- Perform(reply)
      mention <- Say(
        quijote,
        quijoteAccount,
        Tweet(_dictum = "@dulcinea could you follow me to direct messaging?"))
      _ <- Perform(mention)
      follow <- Say(
        dulcinea,
        quijoteAccount,
        Follow())
      Performed(act) <- Perform(follow)
      _ <- Notify(quijote, act.execution.head)
      dm <- Say(
        quijote,
        dulcineaAccount,
        DM(_dictum = "Erm... Soberana y alta señora: El herido de punta..."))
      _ <- Perform(dm)
      rt <- Say(
        dulcinea,
        dulcineaAccount,
        Retweet(_originalTweet = Option(tweet)))
      _ <- Perform(rt)
    } yield ())

    obtained should be(getState())
  }
}
