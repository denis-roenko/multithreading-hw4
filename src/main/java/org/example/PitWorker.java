package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Semaphore;

/**
 * Работник питстопа, меняет шину на прибывшей машине на своем месте
 */
@Log4j2
public class PitWorker extends Thread {

    // Место работника, он же номер колеса от 0 до 3
    private final int position;

    // Ссылка на сущность питстопа для связи
    private final PitStop pitStop;

    @Getter
    private final Semaphore getCarPermission = new Semaphore(1);

    public PitWorker(int position, PitStop pitStop) {
        this.position = position;
        this.pitStop = pitStop;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            log.info("[Питстоп {}] Работник {} ожидает прибытия болида", pitStop.getTeamId(), position);

            // Работник берёт машину в обслуживание при наличии разрешения
            try {
                getCarPermission.acquire();
            } catch (InterruptedException e) {
                currentThread().interrupt();
            }
            F1Car car = pitStop.getCar(); // Работник ждёт прибытия машины на питстоп

            // Работник меняет шину на своей позиции
            log.info("[Питстоп {}] Работник {} приступает к замене колеса болида {}", pitStop.getTeamId(), position, car.getCarId());
            car.getWheel(position).replaceWheel();

            // Работник сообщает о готовности
            log.info("[Питстоп {}] Работник {} завершил замену колеса болида {}", pitStop.getTeamId(), position, car.getCarId());
            pitStop.getWorkersCountDown().countDown();
        }
    }
}
