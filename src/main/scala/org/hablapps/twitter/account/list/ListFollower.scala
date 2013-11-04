package org.hablapps.twitter.account.list

import org.hablapps.{updatable,speech,twitter}
import updatable._

import language.reflectiveCalls

object ListFollower{

  trait State { self: speech.System 
    with twitter.State =>

    trait ListFollower extends Agent {
      type This = Follower
      type Context = TwitterList
      type Player = Tweeter
      type Perform = SocialAction
    }

    implicit val ListFollower = builder[ListFollower]
  }
}
