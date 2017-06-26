package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.start;

import android.content.Context;

/**
 * Created by marcelo on 26/06/17.
 */

public class StartDiscoveringParameters {

    private Context context;

    public StartDiscoveringParameters(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
