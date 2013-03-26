package org.hablapps.twitter

import org.hablapps.updatable._
import org.hablapps.speech

import language.reflectiveCalls

object Twitter {

  trait State { this: speech.System
    with Guest.State
    with account.Account.State =>

    /** Micro-blogging site to answer the question "What is happening?"
      *
      * This is the application entry-point, where users can act as guests to
      * setup new accounts and become Tweeters.
      */
    trait Twitter extends Interaction {
      type This = Twitter

      /** This does not hold any substatus. */
      type Substatus = Nothing

      /** This is a top-interaction, so no context is suitable. */
      type Context = Nothing
      type ContextCol[x] = Traversable[x]

      /** Guests are the only agents authorized to play in this interaction. */
      type Member = Guest
      type MemberCol[x] = List[x]

      /** No resources appear here. */
      type Environment = Nothing
      type EnvironmentCol[x] = Traversable[x]

      /** Guest SpeechActs will be collected here. */
      type Action = SocialAction
      type ActionCol[x] = Traversable[x]

      /** User accounts are the only interaction authorized to be initiated
        * here.
        */
      type Subinteraction = Account
      type SubinteractionCol[x] = Traversable[x]
      
      /** Subinteraction alias (no other interaction hangs here). */
      def accounts = subinteraction.alias[Account]

      /** Member alias (no other agent hangs here). */
      def guests = member
    }

    implicit val Twitter = builder[Twitter]
  }

  trait Rules { this: speech.System with Twitter.State => 
    authorised {
      case Initiate(interaction) if interaction.isA[Twitter] => { 
	implicit state => tops.isEmpty
      }
    }
  }
}
