package org.marceloleite.projetoanna.utils.average;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Calculates the average value of an array on numbers.
 */
public class Average {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Average.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(Average.class);
    }

    /**
     * Total of items on buffer.
     */
    private int itemsOnBuffer;

    /**
     * The buffer which contains the values to calculate the average value.
     */
    private long[] buffer;

    /**
     * The average value of the numbers on buffer.
     */
    private long average;

    /**
     * Constructor.
     *
     * @param bufferSize Size of the buffer which stores the values to calculate the average value.
     */
    public Average(int bufferSize) {
        Log.d(Average.class, LOG_TAG, "Average (43): ");
        this.buffer = new long[bufferSize];
        this.itemsOnBuffer = 0;
        this.average = 0;
    }

    /**
     * Adds a new value to the buffer. If buffer is on its limit it overwrites the its oldest value.
     *
     * @param value Value to add on buffer.
     */
    public void add(long value) {
        buffer[itemsOnBuffer % buffer.length] = value;
        if (itemsOnBuffer < buffer.length) {
            itemsOnBuffer++;
        }
        calculateAverage();
    }

    /**
     * Returns the average value of values stored on buffer.
     *
     * @return The average value of values stored on buffer.
     */
    public long getAverage() {
        return average;
    }

    /**
     * Calculates the average of the values stored on buffer.
     */
    private void calculateAverage() {
        int counter;

        long total = 0;

        for (counter = 0; counter < itemsOnBuffer; counter++) {
            total += buffer[counter];
        }

        average = total / itemsOnBuffer;
    }
}
