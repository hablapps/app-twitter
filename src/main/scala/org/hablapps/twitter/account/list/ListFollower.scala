package org.hablapps.twitter.account.list

import org.hablapps.{updatable,speech,twitter}
import updatable._

import language.reflectiveCalls

object ListFollower{

  trait State { self: speech.System 
    with twitter.State =>

    trait ListFollower extends Agent {
      type This = Follower
      type Substatus = Nothing
      type Context = TwitterList
      type Player = Tweeter
      type Role = Nothing
      type RoleCol[x] = Traversable[x]
      type Perform = SocialAction
      type PerformCol[x] = Traversable[x]
    }

    implicit val ListFollower = builder[ListFollower]

  }

}
