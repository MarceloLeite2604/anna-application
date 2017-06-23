package org.marceloleite.projetoanna.utils;

import android.util.Size;

import java.util.Comparator;

/**
 * Implements the {@link Comparator} interface and compares two sizes by its square area.
 */
public class CompareSizesByArea implements Comparator<Size> {

    @Override
    public int compare(Size operatorA, Size operatorB) {
        long areaA = operatorA.getWidth() * operatorA.getHeight();
        long areaB = operatorB.getWidth() * operatorB.getHeight();

        return Long.signum(areaA - areaB);
    }

}
