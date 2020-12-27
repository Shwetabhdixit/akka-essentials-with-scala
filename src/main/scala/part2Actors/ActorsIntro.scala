package part2Actors

import akka.actor.{Actor, ActorSystem, Props}

import scala.sys.props

object ActorsIntro extends App{
  // part 1 - Actor System (Every scala app has to start with this)
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)
  //actor system is a heavy weight data structure that controls
  //a number of threads under the hood which then allocates to running actors
  //It is recommended to have 1 of these per app, unless we have good reason to
  //have more than 1

  //part 2 - Create Actors
  //kind of like humans talking to each other
  /**
   * Actors are uniquely identified within an actors system
   * Messages are passed and processed async
   * Each actor has a unique way of processing messages i.e. each actor may respond differently
   * Actors are (really) encapsulated.
   */

  //word count actor
  class WordCountActor extends Actor{
    //internal data
    var totalWords=0

    //behaviour
    def receive: PartialFunction[Any,Unit] ={ // can also use Receive in place of PartialFunction[Any,Unit]
      case message: String =>
        println(s"[word Counter] I have received : $message")
        totalWords += message.split(" ").length
      case msg => println(s"[Word Counter] I cannot understand $msg")
    }
  }

  // part 3 - instantiate our actor
  // difference betweeen actors and normal objects in akka is
  //that you cant instantiate an actor by new but by using the actor system

  val wordCounter = actorSystem.actorOf(Props[WordCountActor],"wordCounter")
  val anotherWordCounter = actorSystem.actorOf(Props[WordCountActor],"anotherWordCounter")
  // return type ActorRef - the datatype that akka exposes to you as a programmer
  // so that you cannot call or otherwise poke into the actual wordcount actor instance
  // that akka creates, you can only communicate via the ActorRef

  //part-4 : Communicate with Actor
  wordCounter ! "I am learning akka and its pretty darn cool!" // ! is the name of the method
  //wordCounter.!("I am learning akka and its pretty darn cool!") same as above
  anotherWordCounter ! "A different message"
  // Akka send messages to actors asynchronously

  //Actors are fully encapsulated
  //- You cannot poke at their data, you cannot call their methods and you cannot even instantaite actor classes by hand
  // ! method is called as tell method

  //Instantiating an actor with constructor arguments

  class Person(name:String) extends Actor{
    override def receive: Receive = { // Receive is a partial function from any to unit
      case "hi" => println(s"Hi, my name is $name")
      case _ =>}
  }

  val person = actorSystem.actorOf(Props(new Person("Bob"))) // legal but discouraged
  person ! "hi"

  //best practice for creating actors with constructor arguments
  // 1. Define a companion object
  // 2. Define a method, which based on some arguments , it creates a props object with an actor
  // instance with your constrcutor arguments
  object Person{
    def props(name:String) = Props(new Person(name))
  }

  val person1 = actorSystem.actorOf(Person.props("Hank"))
  person1 ! "hi"

}
