package org.hablapps.twitter.account.list

import org.hablapps.{ updatable, react, speech, twitter}
import updatable._

import language.reflectiveCalls

object TwitterList{

  trait State { self: speech.Program
    with twitter.State =>

    trait TwitterList extends Interaction {
      type This = TwitterList
      
      type Substatus = Nothing

      type Context = Account
      type ContextCol[x] = Option[x]

      type Member = Agent@Union[Listed,ListFollower]
      type MemberCol[x] = List[x]

      type Environment = Nothing
      type EnvironmentCol[x] = Traversable[x]

      type Action = SocialAction
      type ActionCol[x] = Traversable[x]

      type Subinteraction = Nothing
      type SubinteractionCol[x] = Traversable[x]
    }
				  
    implicit val TwitterList = builder[TwitterList]

  }

}
