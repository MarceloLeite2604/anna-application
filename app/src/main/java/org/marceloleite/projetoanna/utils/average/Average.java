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
     * Quantity of items on array.
     */
    private int itemsOnArray;

    /**
     * Stores the values to calculate the average value.
     */
    private long[] array;

    /**
     * The average calculated based on the values stored on array.
     */
    private long average;

    /***
     * Creates a new {@link Average} object.
     *
     * @param arraySize Size of the array to store values and calculate the average value.
     */
    public Average(int arraySize) {
        Log.d(Average.class, LOG_TAG, "Average (43): ");
        this.array = new long[arraySize];
        this.itemsOnArray = 0;
        this.average = 0;
    }

    /**
     * Add a new value on array.
     *
     * @param value The value to be inserted on array.
     */
    public void add(long value) {
        array[itemsOnArray % array.length] = value;
        if (itemsOnArray < array.length) {
            itemsOnArray++;
        }
        calculateAverage();
    }

    /**
     * Returns the average of the values stored.
     *
     * @return The average of the values stored.
     */
    public long getAverage() {
        return average;
    }

    /**
     * Calculates the average of the values stored.
     */
    private void calculateAverage() {
        int counter;

        long total = 0;

        for (counter = 0; counter < itemsOnArray; counter++) {
            total += array[counter];
        }

        average = total / itemsOnArray;
    }
}
