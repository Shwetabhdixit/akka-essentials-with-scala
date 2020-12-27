package part1Recap

import scala.concurrent.Future

object ThreadModelLimitations extends App {
  /*
  Limitations
   */

  /**
   * 1 - OOP encapsulation is ony valid in the single threaded model.
   */

  class BankAccount( private var amount :Int){ // blocks the variable from being accessed by 2 thread simultaneously
    override def toString : String = "" + amount

    def withdraw(money:Int) = this.synchronized{this.amount -= money}
    def deposit(money:Int) = this.synchronized{this.amount += money}
    def getAmount = amount
  }

//  val account = new BankAccount(2000)
//  for(_ <- 1 to 1000){
//    new Thread(() => account.withdraw(1)).start()
//  }
//  for(_ <- 1 to 1000){
//    new Thread(()=>account.deposit(1)).start()
//  }
//  println(account.getAmount)

  //OOP encapsulation is broken in a multi-threaded environment
  //can be solved using synchronization - Locks to the rescue
  //they can introduce deadlocks, livelocks

  // Need a data structure fully encapsulated without locks in a distributed environment

  /**
   * 2) Delegating something to a thread is a pain
   */

  // you have a running thread and you want to pass a runnable to that thread

  var task: Runnable = null

  val runningThread: Thread = new Thread(()=>{
    while(true){
      while(task == null) {
        runningThread.synchronized {
          println("[BackGround] waiting for a task...")
          runningThread.wait()
        }
      }
      task.synchronized{
        println("[Background] I have a task!")
        task.run()
        task = null
      }
    }
  })

  def delegateToBackgroundThread(r: Runnable) ={
    if(task==null) task = r
    runningThread.synchronized{
      runningThread.notify()
    }
  }

  runningThread.start()
  Thread.sleep(500)
  delegateToBackgroundThread(()=>println(42))
  Thread.sleep(1000)
  delegateToBackgroundThread(()=>println("This should run in the background"))

  //Problems
  /**
   * Other singals?
   * Mutliple background tasks and threads
   * Who gave the signal
   * What if a thread crashes
   */

  //Need a Data Structure
  /**
   * Can safely receive messages
   * Can identify the sender
   * Is easily identifiable
   * Can guard against errors
   */

  /**
   * 3) Tracing and dealing with error in a multi-threaded environment is painful.
   */
  // 1M numbers in between 10 threads
  // we can manually write 10 threads and compute on a shared variable, the java way, YUCK!

  import scala.concurrent.ExecutionContext.Implicits.global
  val futures = (1 to 10)
    .map(i => 100000*i until 100000*(i+1)) // 0-9999999, 1000000-199999, ..
    .map(range => Future{
      if (range.contains(546735)) throw new RuntimeException("invalid number")
      range.sum
    })

  val sumFuture = Future.reduceLeft(futures)(_+_) // Future with the sum of all the numbers
  sumFuture.onComplete(println)
  //answer - Failure(java.lang.RuntimeException: invalid number) // very hard to debug in big applications

  /**
   * Thread model limitations
   * OOP is not encapsulated - race conditions
   *
   * Locks used to treat above problem introduce other problems
   * -- deadlocks,livelocks,headaches(literally)
   * -- a massive pain in disributed environments
   *
   * Delegating tasks
   * -- hard, error prone
   * -- never feels "first-class" although often needed
   * -- should never be done in a blocking fashion
   *
   * Dealing with errors - impossible in distributed environments
   */

}
