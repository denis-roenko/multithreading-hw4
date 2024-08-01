package org.example;

import lombok.Getter;

public class Team {

    @Getter
    private final F1Car[] cars = new F1Car[2];
    @Getter
    private final PitStop pitStop;


    public Team(long id) {
        this.pitStop = new PitStop(id);
        for (int i = 0; i < this.cars.length; i++) {
            this.cars[i] = new F1Car(id * 10 + i, pitStop);
        }
        pitStop.start();
    }

    public void prepareRace(Race race) {
        for (F1Car car : this.cars) {
            car.prepareRace(race);
        }
    }
}
