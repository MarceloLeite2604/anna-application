package org.marceloleite.projetoanna.utils.average;

/**
 * Created by marcelo on 22/05/17.
 */

public class Average {

    private int itemsOnBuffer;

    private long[] buffer;

    private long average;

    public Average(int bufferSize) {
        this.buffer = new long[bufferSize];
        this.itemsOnBuffer = 0;
        this.average = 0;
    }

    public void add(long value) {
        buffer[itemsOnBuffer%buffer.length] = value;
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

        for (counter=0; counter < itemsOnBuffer; counter++) {
            total += buffer[counter];
        }

        average = total/itemsOnBuffer;
    }
}
