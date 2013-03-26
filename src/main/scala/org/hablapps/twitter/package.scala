package org.hablapps.twitter

import org.hablapps.speech

object `package` {

  trait Program extends speech.System
      with Twitter.State 
      with Twitter.Rules
      with Guest.State  
      with Guest.Rules
      with account.Account.State 
      with account.Account.Rules 
      with account.Tweeter.State 
      with account.Tweeter.Rules
      with account.Follower.State 
      with account.Tweet.State 
      with account.Tweet.Rules {
    println("Welcome to the Twitter platform!")
  }

  trait PlainSystem extends speech.PlainSystem with Program
}
