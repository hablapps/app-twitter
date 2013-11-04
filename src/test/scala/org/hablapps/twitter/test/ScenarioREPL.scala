package org.hablapps.twitter.test

import org.hablapps.updatable._
import org.hablapps.react
import org.hablapps.speech
import org.hablapps.twitter

object ScenarioREPL extends speech.PlainSystem
  with twitter.Program 
  with react.Debug { 
	 
  val NewEntities(habla: $[Twitter]@unchecked) = 
	 attempt(Initiate(Twitter()))

  val NewEntities(guest1: $[Guest]@unchecked) = 
	 attempt(Play2(Guest(),habla))

  val NewEntities(_, user1Account: $[Account]@unchecked, user1: $[Tweeter]@unchecked) =
    attempt(Say(guest1, habla, SetUpAccount()._new += (Account().name += "user1")))

  val NewEntities(guest2: $[Guest]@unchecked) = 
	 attempt(Play2(Guest(),habla))

  val NewEntities(_, user2Account: $[Account]@unchecked, user2: $[Tweeter]@unchecked) =
    attempt(Say(guest2, habla, SetUpAccount()._new += (Account().name += "user2")))

  val NewEntities(_) = 
    attempt(See(user1, habla, GenObservation(_query = GetAtt(habla,List(Interaction._subinteraction)))))

  val NewEntities(_, user1User2Follower: $[Follower]@unchecked) =
    attempt(Say(user1, user2Account, Follow()))

  val NewEntities(tweet1: $[Tweet]@unchecked) =
    attempt(Say(user2, user2Account, Tweet(_dictum = "hello world!")))

}

