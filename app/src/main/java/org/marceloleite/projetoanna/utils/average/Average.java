package org.marceloleite.projetoanna.utils.average;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by marcelo on 22/05/17.
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

    private int itemsOnBuffer;

    private long[] buffer;

    private long average;

    public Average(int bufferSize) {
        this.buffer = new long[bufferSize];
        this.itemsOnBuffer = 0;
        this.average = 0;
    }

    public void add(long value) {
        buffer[itemsOnBuffer % buffer.length] = value;
        if (itemsOnBuffer < buffer.length) {
            itemsOnBuffer++;
        }
        updateAverage();
    }

    public long getAverage() {
        return average;
    }

    private void updateAverage() {
        int counter;

        long total = 0;

        for (counter = 0; counter < itemsOnBuffer; counter++) {
            total += buffer[counter];
        }

        average = total / itemsOnBuffer;
    }
}
