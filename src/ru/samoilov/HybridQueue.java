// Blocking queue with asynchronous operations put and take...

package ru.samoilov;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;


public class HybridQueue {

  private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

  // Counts of elements by default...
  private int limit = 10;

  public HybridQueue(int limit) {
    this.limit = limit;
  }

  // Add element in blocking queue...
  synchronized void add(Integer element) throws InterruptedException {
    System.out.println("Trying to add: " + element);

    // If queue is full, waiting until someone take element from queue...
    while (queue.size() == limit) {
      System.out.println("Queue is full, waiting until space is free");
      System.out.println();
      System.out.println(Thread.currentThread().getName() + " was end");
      System.out.println();
      wait();
    }

    // If queue is empty, notify and add elements...
    if (queue.size() ==  0) {
      System.out.println("Queue is empty, notify");
      notifyAll();
    }
    queue.add(element);
    System.out.println("Add: " + element );
    System.out.println();
  }


  // Take element from blocking queue...
  synchronized public Integer take() throws InterruptedException {
    System.out.println("Trying to take");

    // If queue is empty, wait until someone add elements...
    while (queue.size() == 0){
      System.out.println("Queue is empty, waiting until something is add");
      wait();
    }

    // If queue is full, notify and take element...
    if (queue.size() == limit) {
      System.out.println("Queue is full, notify");
      notifyAll();
    }
    Integer element = this.queue.remove();
    System.out.println("Take: " + element );
    return element;
  }

}

// Example of working blocking queue...
class Main {

  public static void main(String[] args) throws InterruptedException {

    // Create queue with 5 max elements...
    HybridQueue queue = new HybridQueue(5);

    // Create thread with producer and make delay 1 sec...
    Thread producer = new Thread(new Producer(queue));
    producer.setName("Producer");
    producer.start();
    Thread.currentThread().sleep(1000);

    // Create thread with consumer...
    Thread consumer = new Thread(new Consumer(queue));
    consumer.setName("Consumer");
    consumer.start();
  }

  static class Producer implements Runnable {
    private final HybridQueue queue;
    private Random random = new Random();

    public Producer(HybridQueue queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      System.out.println("Producer run");
      System.out.println();
      while (true) {
        try {
          queue.add(produce());
          Thread.currentThread().sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    private Integer produce() {
      Integer i = random.nextInt(100);
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
      Integer i = queue.take();
      System.out.println("Consumer consumed: " + i);
      System.out.println();
    }
  }

}

//  Example of asynchronous working of queue...
class OtherThread extends Thread
{
  private final HybridQueue queue;
  private Random random = new Random();

  public OtherThread(HybridQueue queue) {
    this.queue = queue;
  }

  @Override
  public void run()
  {
    for(int i = 0; i < 5; i++) {
      try {
        sleep(1000);
      } catch(InterruptedException e){}

      System.out.println("OtherThread!");
      try {
        queue.add(produce());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Integer produce() {
    Integer i = random.nextInt(100);
    System.out.println("Producer produce: " + i);
    return i;
  }
}

// Example of asynchronous working queue...
class MainThread
{
  static OtherThread otherThread;
  private final HybridQueue queue;

  public MainThread(HybridQueue queue) {
    this.queue = queue;
  }

  public static void main(String[] args) throws InterruptedException {

    // Create queue with 5 max elements...
    HybridQueue queue = new HybridQueue(5);

    // Set name main thread...
    Thread.currentThread().setName("MainThread");

    // Create other thread and set him name...
    otherThread = new OtherThread(queue);
    System.out.println("Discussion begins...");
    otherThread.start();
    otherThread.setName("OtherThread");

    Random random = new Random();

    for(int i = 0; i < 5; i++)
    {
      try{
        Thread.sleep(1000);
      }catch(InterruptedException e){}

      System.out.println("MainThread!");
      Integer element = random.nextInt(100);
      System.out.println("Producer produce: " + element);
      queue.add(element);
    }

  }
}