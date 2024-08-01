package org.example;

import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class Main {
    public static void main(String[] args) {
        Team[] teams = new Team[3];

        for (int i = 0; i < teams.length; i++) {
            teams[i] = new Team(i + 1);
        }

        Race race = new Race(1000, teams);

        race.start();
        race.printResults();

        // Завершаем работу питстопов
        Arrays.stream(teams)
                .map(Team::getPitStop)
                .forEach(Thread::interrupt);
        log.info("Гонка завершена!");
    }
}
