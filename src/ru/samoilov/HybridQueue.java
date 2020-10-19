//Блокирующая очередь с возможность асинхронной операции вставки, удаления...

package ru.samoilov;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;


// Интерфейс для асинхронной работы вставки в очередь...
interface pushListener {
  void push(Collection<Object> queue);
}


class HybridQueue {

  // Блокирующая потокобезопасная коллекция...
  Collection<Object> queue = Collections.synchronizedCollection(new LinkedBlockingQueue<>());

  // Поле слушателя...
  private pushListener listener;

  // Получение слушателя...
  public pushListener getListener() {
    return listener;
  }

  // Настройка слушателя...
  public void setListener(pushListener listener) {
    this.listener = listener;
  }

  // Счетчик элемента...
  int k = 0;

  // Обработка добавления элементов в очередь...
  Runnable listOperations = () -> {
    for(int i = k; i < k + 100; i++){
      queue.add(i);
    }
    k += 100;
  };

  // Добавление элементов в потоке...
  public Runnable add(int i){
    return listOperations;
  }

  // Ассинхронное добавление элементов...
  public void push() throws InterruptedException {

    // Создаем поток для добавления элементов с вызовом callback функции...
    Thread thread = new Thread(new Runnable() {
      public void run() {
        for (int i = 200; i < 300; i++) {
          queue.add(i);
        }

        // Проверяем, зарегистрирован ли слушатель,
        // если да, добавляем элементы через функцию...
        if (listener != null) {
          // Метод обратного вызова класса...
          listener.push(queue);
        }
      }
    });

    // Запускаем поток на выполнение...
    thread.start();

    // Ожидаем завершения работы потока...
    thread.join();
  }

  // Вывод элементов очереди...
  public void print(){
    Iterator iterator = queue.iterator();

    while(iterator.hasNext()){
      System.out.println(iterator.next());
    }
  }

}

// Класс с CallBack методом...
class QueueAsync implements pushListener {
  public void push(Collection<Object> queue)
  {
    for(int i = 300; i < 400; i++){
      queue.add(i);
    }
  }
}