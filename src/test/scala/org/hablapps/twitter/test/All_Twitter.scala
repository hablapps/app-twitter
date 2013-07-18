package org.hablapps.twitter.test

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Suites

@RunWith(classOf[JUnitRunner])
class All_Twitter extends Suites(
  new FollowScenarioTest(followScenarioSystem),
  new TweetScenarioTest(tweetScenarioSystem),
  new RegisterScenarioTest(registerScenarioSystem))
