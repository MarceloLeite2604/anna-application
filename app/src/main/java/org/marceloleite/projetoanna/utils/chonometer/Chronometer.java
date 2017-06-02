package org.marceloleite.projetoanna.utils.chonometer;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 13/05/2017.
 */

public class Chronometer {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Chronometer.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(Chronometer.class);
    }

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
