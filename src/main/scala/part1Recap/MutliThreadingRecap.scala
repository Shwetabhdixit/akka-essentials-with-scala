package part1Recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MutliThreadingRecap extends App{
  // creating threads on the JVM

  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("I am running in parallel")
  })

  val syntacticSugarThread = new Thread(() => println("I am running in parallel"))

  syntacticSugarThread.start()
  syntacticSugarThread.join()

  val threadHello = new Thread(() => (1 to 1000).foreach(_ => println("hello")))
  val threadBye = new Thread(() => (1 to 1000).foreach(_ => println("Bye")))

  threadHello.start()
  threadBye.start()

  //different runs produce different results

  class BankAccount(@volatile private var amount :Int){ // blocks the variable from being accessed by 2 thread simultaneously
    override def toString : String = "" + amount

    def withdraw(money:Int) = this.amount -= money

    def safeWithdraw(money:Int) = this.synchronized{
      this.amount -=money
    }
  }

  //inter-thread communication on the JVM
  // wait -notify mechanism

  //scala Futures
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future{
    ///long computation - evaluated on a differet thread
    42
  }

  //callbacks

  future.onComplete{
    case Success(42) => println("I found the value of life, lol")
    case Failure(_) => println("Uh oh,we made a doody")
  }

  val aProcessedFuture = future.map(_ + 1) // Future with 43
  val aFlatFuture = future.flatMap{
    value => Future(value+2)
  } //Future with 44

  val filterFuture = future.filter(_%2 ==0) // No Suchelement exception, if does not qualify

  //for Comprehensions

  val aNonsenseFuture = for{
    meaningOfLife <- future
    filteredMeaning <- filterFuture
  } yield meaningOfLife + filteredMeaning

  // utitlities for future -> andThen, recovering / recoverWith

  //Promises ( advanced scala course ) - completing Futures "manaually"
}
