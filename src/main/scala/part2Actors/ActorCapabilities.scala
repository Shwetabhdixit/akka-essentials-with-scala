package part2Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2Actors.ActorCapabilities.Person.LiveTheLife

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    context // it is a complex data structure that has references to info. regarding the environment
    //this actor runs in. Has access to this actor's own reference, equivalent of this

    override def receive: Receive = {
      case "Hi" => context.sender() ! "Hello There" // replying to a message
      case message: String => println(s"${context.self} I have received $message")
      case number: Int => println(s"[Simple Actor] I have received a NUMBER: $number")
      case SpecialMessage(contents) => println(s"[Simple Actor] I have received something special $contents")
      case SendMessageToYourself(content) => self ! content // sending messages to our actor , this will trigger the
        //case message option, since the content is a string
      case SayHiTo(ref) => ref ! "Hi" // (ref ! "Hi")(self)
      case WirelessPhoneMessage(content,ref) => ref forward (content+"s") // I keep the original sender of the WPM
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor],"simpleActor")

  simpleActor ! "Hello,Actor"

  // 1- messages can be of any type
  //a) Messages must be IMMUTABLE
  //b) Messages must be SERIALIZABLE ( JVM can convert it to a bytestream and transfer to another JVM)
  // in practice use case classes and case objects
  simpleActor ! 42
  // when we invoke the tell method , akka retrieves the object defined under receive
  //it will then invoke the apt case according to Object type

  case class SpecialMessage(contents: String) // can invoke custom data types also

  simpleActor ! SpecialMessage("Some Special Message")

  //2) Actors have information about their context and about themselves
  //context.self === 'this' in OOP
  //context.self === self

  case class SendMessageToYourself(content:String)
  simpleActor ! SendMessageToYourself("Hey there , I am an actor")

  // 3) Actors can REPLY to messages

  val alice =  system.actorOf(Props[SimpleActor],"alice")
  val bob = system.actorOf(Props[SimpleActor],name="bob")

  case class SayHiTo(ref :ActorRef)

  alice ! SayHiTo(bob)

  // context.sender() contains the Actor reference about the actor who last sent a message to me
  //def !(message: Any)(implicit sender: ActorRef = Actor.noSender): Unit
  // this means that when we an actor sends a message ,the implicit sender is specified as "self"
  //like on line 18
  //when sending the message, which as we see in the method declaration is null by default

  // 4 - deadLetters
  alice ! "Hi" // reply to "me"
  // we get dealLetters info log, saying the message was not delivered, that is what is meant
  //by Actor.noSender ( which is null )
  // if there is no sender the reply will go to deadLetters

  //5 - Forwarding messages
  //D -> A -> B
  //forwarding -> sending the message with the ORIGINAL sender

  case class WirelessPhoneMessage(content: String, ref:ActorRef)
  alice ! WirelessPhoneMessage("Hi",bob)
  // Output is : Actor[akka://actorCapabilitiesDemo/user/bob#341690381] I have received His
  // This is because alice 'forwards' Hi with s at the end, but the sender remains Bob, since we have used 'forward'
  // Bob receives the distorted message 'His' , with the original sender 'noSender'

  /**
   * Excercises
   * 1) A counter actor
   *   -- increment
   *   -- decrement
   *   -- print
   *
   * 2) A Bank account as an actor
   * receives
   *  -- Deposit an amount
   *  -- Withdraw an amount
   *  -- Statement
   *  Replies
   *  -- Success or Failure Messages of operations
   *
   *  Interact with some other kind of actor
   */

  //EXERCISE 1 -  MY IMPLEMENTATION
  //case class Increment(a:Int)
  //case class Decrement(b:Int)

//  class Counter extends Actor{
//    var counter :Int = 0
//    def receive : Receive = {
//      case Increment(a) => {counter+=a ; println(s"[counter1] The counter is now at $counter")}
//      case Decrement(b) => {counter-=b;println(s"[counter1] The counter is now at $counter")}
//      case message : String => println(s"[counter1] Got the command $message . The counter is at $counter")
//    }
//  }

  //val counter1 = system.actorOf(Props[Counter],"counter1")

  //counter1 ! Increment(2)
  //counter1 ! Increment(3)
  //counter1 ! Decrement(1)
  //counter1 ! "Tell me the counter"

  //DANIEL's IMPLEMENTATION

  //DOMAIN of the counter
  object Counter{
    case object Increment
    case object Decrement
    case object Print
  }
  class Counter extends Actor{
    import Counter._
    var count =0;
    override def receive: Receive ={
      case Increment => count+=1
      case Decrement => count-=1
      case Print => println(s"[Counter] My current count is $count")
    }
  }
  val counter = system.actorOf(Props[Counter],"myCounter")
  import Counter._
  (1 to 5).foreach(_ => counter! Increment)
  (1 to 3).foreach(_ => counter! Decrement)
  counter ! Print

  //EXERCISE 2

  object BankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object Statement

    case class TransactionSucess(message:String)
    case class TransactionFailure(message:String)
  }
  import BankAccount._
  class BankAccount extends Actor{
    import BankAccount._
    var funds: Int  = 0
    override def receive: Receive ={
      case Deposit(amount) => {
        if (amount < 0) sender() ! TransactionFailure("invalid deposit amount")
        else {
          funds += amount
          sender() ! TransactionSucess("Successfully deposited amount")
        }
      }
        case Withdraw(amount) =>{
          if(amount<0) sender() ! TransactionFailure(s"invalid deposit $amount")
          else if (amount>funds) sender() ! TransactionFailure("Insufficient funds")
          else
            {
              funds -= amount
              sender() ! TransactionSucess(s"Successfully withdrew $amount")
            }
        }
      case Statement => sender() ! s"Your balance is $funds"
    }
  }

  object Person{
    case class LiveTheLife(account: ActorRef)
  }
  class Person extends Actor{
    import Person._

    override def receive: Receive = {
      case LiveTheLife(account) =>{
        account ! Deposit(10000)
        account ! Withdraw(50000)
        account ! Withdraw(500)
        account ! Statement
      }
      case message => println(message.toString)
    }
  }

  val account  = system.actorOf(Props[BankAccount],"bankAccount")
  val person = system.actorOf(Props[Person],"billionaire")

  person ! LiveTheLife(account)
}
