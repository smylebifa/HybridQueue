//Блокирующая очередь с возможность асинхронной операции вставки, удаления...

package ru.samoilov;

import java.util.concurrent.LinkedBlockingQueue;

public class  HybridQueue {


  private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

  private int  limit = 10;

  public HybridQueue(int limit){
    this.limit = limit;
  }

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

  // Удаление элемента...
  synchronized public Integer pop() throws InterruptedException {
    System.out.println("Trying to take");
    while (queue.size() == 0){
      System.out.println("Queue is empty, waiting until something is put");
      wait();
    }
    if (queue.size() == limit){
      System.out.println("Queue is full, notify");
      notifyAll();
    }
    Integer element = this.queue.remove();
    System.out.println("Take: " + element );
    return element;
  }

}