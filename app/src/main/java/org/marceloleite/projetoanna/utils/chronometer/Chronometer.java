package org.marceloleite.projetoanna.utils.chronometer;

/**
 * Created by Marcelo Leite on 13/05/2017.
 */

public class Chronometer {

    private long startTime;

    private long stopTime;

    public Chronometer() {
        this.stopTime = 0;
        this.startTime = 0;
    }

    public void start() {
        this.startTime = System.nanoTime();
    }

    public void stop() {
        this.stopTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public long getDifference() {
        return stopTime - startTime;
    }
}
