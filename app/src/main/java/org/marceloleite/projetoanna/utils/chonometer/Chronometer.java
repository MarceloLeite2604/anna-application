package org.marceloleite.projetoanna.utils.chonometer;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A chronometer to calculate the difference between times.
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

    /**
     * The time which the chronometer was started (in nanoseconds).
     */
    private long startTime;

    /**
     * The time which the chronometer was stopped (in nanoseconds).
     */
    private long stopTime;

    /**
     * Constructor.
     */
    public Chronometer() {
        Log.d(Chronometer.class, LOG_TAG, "Chronometer (36): ");
        this.stopTime = 0;
        this.startTime = 0;
    }

    /**
     * Starts the chronometer.
     */
    public void start() {
        this.startTime = System.nanoTime();
    }

    /**
     * Stops the chronometer.
     */
    public void stop() {
        this.stopTime = System.nanoTime();
    }

    /**
     * Retrieves the difference between the chronometer's stop and start time.
     *
     * @return The difference between the chronometer's stop and start time.
     */
    public long getDifference() {
        return stopTime - startTime;
    }
}
