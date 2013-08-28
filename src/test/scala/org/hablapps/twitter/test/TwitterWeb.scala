package org.hablapps.twitter.test

import org.hablapps.{ updatable, react, speech }
import updatable._

import org.hablapps.twitter

object TwitterWeb extends App {

  object System extends speech.web.PlainSystem with twitter.Program {
    reset(for {
      habla <- Initiate(Twitter(_name = Some("habla")))
      accountJserrano <- Initiate(Account(_name = Some("jserrano")), habla)
      jserrano <- Play(Tweeter(_name = Some("user")), accountJserrano)
    } yield ())

    serverHttpPort = 8888
  }

  System.launch

}
