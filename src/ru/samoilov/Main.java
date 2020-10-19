package ru.samoilov;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        HybridQueue queue = new HybridQueue();

//    Thread thread1 = new Thread(queue.add(100));
//    Thread thread2 = new Thread(queue.add(200));
//
//    thread1.start();
//    thread2.start();
//
//    thread1.join();
//    thread2.join();


        pushListener mListener = new QueueAsync();
        queue.setListener(mListener);
        queue.push();

        queue.print();

    }

}
