package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

@NoArgsConstructor
public class Wheel {

    @Getter
    private int status = 100;


    public void replaceWheel() {
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
        status = 100;
    }

    public void travel(long speed) {
        status -= Math.floor((25 * speed / 130f) + Math.random() * 5f);
    }
}
