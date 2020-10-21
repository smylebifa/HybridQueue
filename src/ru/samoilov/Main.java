package ru.samoilov;

import java.util.Random;

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
