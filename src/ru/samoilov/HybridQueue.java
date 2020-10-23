//Блокирующая очередь с возможность асинхронной операции вставки, удаления...

package ru.samoilov;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class  HybridQueue {

  private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

  // Counts of elements by default...
  private int  limit = 10;

  public HybridQueue(int limit) {
    this.limit = limit;
  }

  //  Add element in blocking queue...
  synchronized void push(Integer element) throws InterruptedException {
    System.out.println("Trying to put: " + element );
    while (queue.size() == limit) {
      System.out.println("Queue is full, waiting until space is free");
      System.out.println();
      wait();
    }
    if (queue.size() ==  0) {
      System.out.println("Queue is empty, notify");
      notifyAll();
    }
    queue.add(element);
    System.out.println("Put: " + element );
    System.out.println();
  }

  //  Delete element from blocking queue...
  synchronized public Integer pop() throws InterruptedException {
    System.out.println("Trying to take");
    while (queue.size() == 0){
      System.out.println("Queue is empty, waiting until something is put");
      wait();
    }
    if (queue.size() == limit) {
      System.out.println("Queue is full, notify");
      notifyAll();
    }
    Integer element = this.queue.remove();
    System.out.println("Take: " + element );
    return element;
  }

  public void put(Integer element) {
    queue.add(element);
  }

  public Integer take() {
    if (queue.size() == 0)
      return 0;
    else
      return queue.remove();
  }

}

//  Example of working blocking queue...
class Main {

  public static void main(String[] args) throws InterruptedException {

    HybridQueue queue = new HybridQueue(5);
    new Thread(new Producer(queue)).start();
    Thread.currentThread().sleep(1000);
    new Thread(new Consumer(queue)).start();

  }

  static class Producer implements Runnable {
    private final HybridQueue queue;

    public Producer(HybridQueue queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      System.out.println("Producer run");
      System.out.println();
      while (true) {
        try {
          queue.push(produce());
          Thread.currentThread().sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    private Integer produce() {
      Integer i = new Random().nextInt(100);
      System.out.println("Producer produce: " + i);
      return i;
    }
  }

  static class Consumer implements Runnable {
    private final HybridQueue queue;

    public Consumer(HybridQueue queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      System.out.println("Consumer run");
      System.out.println();
      while (true) {
        try {
          consume();
          Thread.currentThread().sleep(1500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    private void consume() throws InterruptedException {
      Integer i = queue.pop();
      System.out.println("Consumer consumed: " + i);
      System.out.println();
    }
  }

}

//  Example of asynchronous working of queue... 
class EggVoice extends Thread
{
  private final HybridQueue queue;

  public EggVoice(HybridQueue queue) {
    this.queue = queue;
  }

  @Override
  public void run()
  {
    for(int i = 0; i < 5; i++) {
      try {
        sleep(1000);
      } catch(InterruptedException e){}

      System.out.println("Egg!");
      queue.put(i);
    }
  }
}

class ChickenVoice
{
  static EggVoice mAnotherOpinion;
  private final HybridQueue queue;

  public ChickenVoice(HybridQueue queue) {
    this.queue = queue;
  }


  public static void main(String[] args)
  {
    HybridQueue queue = new HybridQueue(5);
    mAnotherOpinion = new EggVoice(queue);
    System.out.println("Discussion begins...");
    mAnotherOpinion.start();

    for(int i = 0; i < 5; i++)
    {
      try{
        Thread.sleep(1000);
      }catch(InterruptedException e){}

      System.out.println("Chicken!");
      queue.put(i);
    }


    if(mAnotherOpinion.isAlive()) // If opponent, who say "egg!" not finished...
    {
      try{
        mAnotherOpinion.join(); //  Wait opponent...
      }catch(InterruptedException e){}

      System.out.println("First was egg!");
    }
    else  //  Else opponent is finished to saying...
    {
      System.out.println("First was chicken!");
    }
    System.out.println("Discussion complete!");
  }
}


