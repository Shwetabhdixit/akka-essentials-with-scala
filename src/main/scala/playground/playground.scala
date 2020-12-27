package playground

import akka.actor.ActorSystem

object playground extends App{

  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
  /**
   * Traditional Objects
   * -- we store their state as data
   * -- we call their methods
   *
   * Actors
   * -- we store their state as data
   * -- we send messages to them, asynchronously
   * ACTORS ARE OBJECTS WE CANT ACCESS DIRECTLY , BUT ONLY SEND MESSAGES TO
   */
}
