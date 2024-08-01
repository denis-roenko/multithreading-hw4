package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;

@Log4j2
public class PitStop extends Thread {

    @Getter
    private final long teamId;
    private final PitWorker[] workers = new PitWorker[4];
    private final Semaphore carLimit = new Semaphore(1);
    @Getter
    private CountDownLatch workersCountDown;

    private final AtomicReference<F1Car> currentCar = new AtomicReference<>(null);

    public PitStop(long teamId) {
        this.teamId = teamId;
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new PitWorker(i, this);
            workers[i].start();
        }
    }

    public void pitline(F1Car f1Car) {
        try {
            log.info("[Питстоп {}] Болид {} прибыл на питстоп", teamId, f1Car.getCarId());
            carLimit.acquire(); // Захватываем блокировку для одного болида

            // Помещаем болид на обслуживание
            currentCar.compareAndSet(null, f1Car);
            workersCountDown = new CountDownLatch(workers.length);

            synchronized (this) {
                notifyAll(); // Уведомляем работников о прибытии болида
            }
            workersCountDown.await(); // Ожидаем пока все работники не заменят колёса
        } catch (InterruptedException e) {
            currentThread().interrupt();
        } finally {
            // Отпускаем машину с питстопа, открываем его для следующей
            currentCar.compareAndSet(f1Car, null);
            carLimit.release();
            log.info("[Питстоп {}] Болид {} покинул питстоп", teamId, f1Car.getCarId());

            // Позволяем рабочим брать в обслуживание следующую машину
            Arrays.stream(workers).forEach(worker -> worker.getGetCarPermission().release());
        }
    }


    @Override
    public void run() {
        while (!isInterrupted()) {
            //синхронизируем поступающие болиды и работников питстопа при необходимости
        }
        Arrays.stream(workers).forEach(Thread::interrupt);
    }

    public synchronized F1Car getCar() {
        // Блокируем поток до момента поступления машины на питстоп и возвращаем ее
        while (isNull(currentCar.get())) {
            try {
                wait(); // Ожидаем пока болид не прибудет на питстоп
            } catch (InterruptedException e) {
                currentThread().interrupt();
            }
        }
        return currentCar.get();
    }
}
