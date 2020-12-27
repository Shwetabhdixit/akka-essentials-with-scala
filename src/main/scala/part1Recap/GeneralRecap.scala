package part1Recap

import scala.util.Try

object GeneralRecap extends App{
  val aCondition : Boolean = false
  var aVariable = 42
  aVariable =1

  //expressions

  val aConditionedVal = if(aCondition) 42 else 65

  //code block
  val aCodeBlock = {
    if(aCondition) 74
    56 // result of a code block is its last expression, in this case 56
  }

  //types

  //Unit -> type of expression for side effects, they do something, but don't return anything meaningful
  //example
  val theUnit = println("hello")

  def aFunction(x:Int):Int = x+1

  //recursion - > TAIL RECURSION
  // it is an optimization from the scala compiler, in that it rewrites recursive calls so that it does not
  //throw stackoverflow errors due to excessive number of recursive calls

  //OOP

  class Animal

  class Dog extends Animal
  val aDog: Animal = new Dog

  trait carnivore{
    def eat(a:Animal): Unit = ???
  }

  class Crocodile extends Animal with carnivore{
    override def eat(a: Animal): Unit = println("crunch")
  }

  //method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  //anonymous classes

  val aCarnivore = new carnivore {
    override def eat(a: Animal): Unit = println("ROAR")
  }
  aCarnivore eat aDog

  //generics
  abstract class MyList[+A]

  //companion object
  object MyList

  //case classes
  case class Person(name:String,age:Int)

  //exceptions
  val aPotentialFailure = try{
    throw new RuntimeException("Ouch") //returns Nothing
  }catch {
    case e:Exception => "Caught you"
  }finally {
    //side effects
    println("some logs")
  }

  //Functional Programming
  val incrementor = new Function1[Int,Int]{
    override def apply(v1: Int): Int = v1+1
  }

  val incremented = incrementor(42) // whenever scala sees an objects is being called like a function
  //it returns the output from its apply method
  //incrementor.apply(42)

  val anonIncrementor = (x:Int) => x+1
  // Int => Int === Function1[Int,Int]
  //FP is all about working with functions as first class (citizens)

  List(1,2,3).map(anonIncrementor)
  //map == HOF(Higher Order Function) since it takes a function as a parameter
  //and returns another function

  //for comprehensions
  val pairs = for{
    num <- List(1,2,3,4)
    char <- List('a','b','c','d')
  }yield num + "-"+ char

  // List(1,2,3,4).flatMap(num => List('a','b','c','d').map(char => num +"-"+char))

  //Seq , Array, List, Vector, Map, Tuples, Sets

  //Options and Try

  val anOption = Some(2)
  val aTry = Try{
    throw new RuntimeException
  }

  //pattern matching
  val unknown = 2
  val order = unknown match{
    case 1=> "first"
    case 2 => "second"
    case _ => "unknown"
  }
  val bob = Person("bob",22)
  val greeting = bob match{
    case Person(n,_) => s"Hello , my name is $n"
    case _ => "I don't know my name"
  }
}
