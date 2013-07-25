package org.hablapps.twitter.account.list

import org.hablapps.{ updatable, react, speech, twitter}
import updatable._

import language.reflectiveCalls

object TwitterList{

  trait State { self: speech.Program
    with twitter.State =>

    trait TwitterList extends Interaction {
      type This = TwitterList
      type Context = Account
      type ContextCol[x] = Option[x]
      type Member = Agent
      type MemberCol[x] = List[x]
      type Action = SocialAction
    }
          
    implicit val TwitterList = builder[TwitterList]
  }
}
