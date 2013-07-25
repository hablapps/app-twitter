package org.hablapps.twitter.account.list

import org.hablapps.{updatable,speech,twitter}
import updatable._

import language.reflectiveCalls

object Listed {

  trait State { self: speech.System 
    with twitter.State =>

    trait Listed extends Agent {
      type This = Listed
      type Context = TwitterList
      type Player = Tweeter
      type Perform = SocialAction
    }

    implicit val Listed = builder[Listed]
  }
}
