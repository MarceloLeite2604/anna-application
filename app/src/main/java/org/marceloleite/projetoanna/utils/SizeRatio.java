package org.marceloleite.projetoanna.utils;

import android.util.Size;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public abstract class SizeRatio {

    private SizeRatio() {
    }

    public static double getSizeRatio(Size size) {
        return (double) size.getWidth() / (double) size.getHeight();
    }
}
