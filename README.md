## TWITTER APP 

This an implementation of the Twitter social network in the [Speech]
(http://speechlang.org) programming language. The purpose of this
implementation is simply to illustrate how the *functional
requirements* of typical Web 2.0 apps can be programmed in
Speech. Thus, we abstract away from non-functional concerns relating
to the persistence, presentation and web layers, and focus instead on
the business logic of Twitter. You can find explanations on the design
of this app in the Speech [user
guide](http://speechlang.org). Currently, the implementation is far
from being complete, but it will eventually cover the 100% of these
requirements. We promise!

## Compilation

To compile the twitter source code simply follow these steps:

#### Download twitter 

To download these sources, you must obtain [git](http://git-scm.com/)
and clone the app-twitter repository.

```shell 
> git clone http://github.com/hablapps/app-twitter <twitter>
```

#### Install sbt 

The app-twitter project is configured with the sbt build tool. To
install sbt follow the instructions at <https://github.com/sbt/sbt>.

#### Compile twitter 

```shell
$ cd <twitter>
$ sbt 
> test:compile
```

## Run twitter

From sbt, you can run some tests with the `test-only` command:

```shell
> test-only org.hablapps.twitter.test.All_Twitter
...
```

Alternatively, you can launch the Speech interpreter and simulate your
own scenario:

```shell
$ cd <twitter>
$ sbt
> console
scala> import org.hablapps.{speech,twitter}
...
scala> val MyTwitter = new speech.PlainSystem with twitter.Program
Welcome to the twitter platform!
...
scala> import MyTwitter._
...
scala> val NewEntities(mytwitter: $[Twitter]) = attempt(Initiate(Twitter()))
...
```

## License

This software is released under Apache License, Version 2.0.

