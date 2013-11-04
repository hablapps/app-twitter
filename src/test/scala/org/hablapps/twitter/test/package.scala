package org.hablapps.twitter.test

import org.hablapps.speech
import speech._
import org.hablapps.twitter

object `package` {

  trait System extends speech.PlainSystem with twitter.Program {
  	date = () => default_date
  }

  val followScenarioSystem = new System {}

  val tweetScenarioSystem = new System {}

  val registerScenarioSystem = new System {} 
}
