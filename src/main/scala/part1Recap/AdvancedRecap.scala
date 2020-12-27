package part1Recap

import scala.concurrent.Future

object AdvancedRecap extends App{
  // partial functions -> functions that operate only on a subset of that given input domain
  val partialFunction: PartialFunction[Int,Int] ={
    case 1=> 42
    case 3 => 65
    case 5 => 99
  }
  //any other input and this partial function will throw an exception

  //equivalently
  val pf = (x:Int) => x match{
    case 1 => 42
    case 3 => 65
    case 5 => 99
  }
  val function :(Int => Int) = partialFunction

  val modifiedList = List(1,2,3)map{
    case 1=> 42
    case _ => 0
  }

  //lifting
  val lifted = partialFunction.lift // total function Int => Option[Int]
  lifted(3) // Some(65)
  lifted(88) // None , partial function itself would return a match error

  val pfChain = partialFunction.orElse[Int,Int]{
    case 60 => 9000
  }

  pfChain(5) // 999 per partialFunction
  pfChain(60) //9000 -> due to the pfChain implementation
  pfChain(457) // throw a match error

  //type aliases

  type ReceiveFunction = PartialFunction[Any,Unit]

  def receive: ReceiveFunction ={
    case 1 => println("Hello")
    case _ => println("Confused..")
  }

  //implicits

  implicit val timeout = 3000
  def setTimeout(f: () => Unit)(implicit timeout:Int) = f()
  setTimeout(() => println("Timeout")) // extra parameter list omitted since we already have an implicit
  //timeout val defined

  //implicit coversions
  //1) implicit defs

  case class Person(name :String){
    def greet = s"Hi my name is $name"
  }

  implicit def fromStringToPerson(string :String) :Person = Person(string)

  "peter".greet
  //for the complier its equivalent to //fromStringToPerson("Peter").greet // automatically done by the compiler

  //2) Implicit class

  implicit class Dog(name:String){
    def bark = println("bark")
  }

  "Lassie".bark
  //new Dog("Lassie").bark //automatically done by the compiler

  //organize
  //local scope
  implicit val inverseOrdering :Ordering[Int] = Ordering.fromLessThan(_>_)
  List(1,2,3).sorted

  //imported scope
  import scala.concurrent.ExecutionContext.Implicits.global

  val future = Future{
    println("hello future") // read up , what is future
  }
  //order used
  //1) local scope
  //2) imported scope
  //3) companion objects of the types included int the call

  object Person{
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a,b) =>
      a.name.compareTo(b.name) < 0)
  }

  List(Person("Bob"), Person("Alice")).sorted // List(Person("Alice"),Person("Bob"))


}