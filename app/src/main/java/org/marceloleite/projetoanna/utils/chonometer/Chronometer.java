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
        Log.addClassToLog(Chronometer.class);
    }

    /**
     * The chronometer start time.
     */
    private long startTime;

    /**
     * The chronometer stop time.
     */
    private long stopTime;

    /**
     * Creates a {@link Chronometer} object.
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
     * Returns the difference between stop and start time on chronometer.
     *
     * @return The difference between stop and start time on chronometer.
     */
    public long getDifference() {
        return stopTime - startTime;
    }
}
