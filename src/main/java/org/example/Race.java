package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Log4j2
public class Race {

    @Getter
    private final long distance;
    private long startTime;

    @Getter
    private final CountDownLatch startSignal = new CountDownLatch(1);

    private final List<F1Car> participantCars = new java.util.ArrayList<>();

    private final List<Team> teams = new java.util.ArrayList<>();

    public Race(long distance, Team[] teams) {
        this.distance = distance;
        this.teams.addAll(List.of(teams));
        Arrays.stream(teams)
                .flatMap(team -> Arrays.stream(team.getCars()))
                .forEach(this::register);
    }

    /**
     * Запускаем гонку
     */
    public void start() {
        for (Team team : teams) {
            team.prepareRace(this);
        }
        // Даем команду на старт гонки
        log.info("Гонка началась!");
        startTime = Instant.now().toEpochMilli();
        startSignal.countDown();

        // Ожидаем завершения гонки
        waitRaceToFinish();
    }


    // Регистрируем участников гонки
    public void register(F1Car participantCar) {
        participantCars.add(participantCar);
    }


    public void start(F1Car f1Car) {
        // Фиксация времени старта
    }

    public long finish(F1Car participant) {
        // Фиксация времени финиша
        val finishTime = Instant.now().toEpochMilli();
        return finishTime - startTime; //длительность гонки у данного участника
    }

    public void printResults() {
        participantCars.sort(F1Car::compareTo);
        log.info("Результат гонки:");
        int position = 1;
        for (F1Car participant : participantCars) {
            log.info("Позиция: {}, номер участника: {}, время: {} мс.", position++, participant.getName(), participant.getTime());
        }
    }

    private void waitRaceToFinish() {
        Consumer<F1Car> waitCarToFinish = car -> {
            try {
                car.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        teams.stream()
                .flatMap(team -> Arrays.stream(team.getCars()))
                .forEach(waitCarToFinish);
    }
}
