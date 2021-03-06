package org.marceloleite.projetoanna.utils.chonometer;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A chronometer object which stores the time it had been started, stopped and returns the difference between these two instants.
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
        Log.addClassToLog(LOG_TAG);
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
     * Returns the difference between the chronometer's stop and start time.
     *
     * @return The difference between the chronometer's stop and start time.
     */
    public long getDifference() {
        return stopTime - startTime;
    }
}
