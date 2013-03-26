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
class TweetScenarioTest extends FunSpec
    with ShouldMatchers
    with BeforeAndAfter {

  describe("Follow Scenario") {

    it("should run smoothly") {
      
      object System extends speech.PlainSystem
        with twitter.Program
        with react.Debug

      import System._

      show_final_state = true

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
      	DM(_dictum = "Soberana y alta señora: El herido de punta..."))
      ) should be(None)

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
	Follow(__new = Some(Follower()))))

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
	  Follow(__new = Some(Follower())))
	_ <- Perform(follow)
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
}
