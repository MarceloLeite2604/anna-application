package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching;

import android.content.Context;

/**
 * Created by marcelo on 26/06/17.
 */

public class SearchingDevicesParameters {

    private Context context;

    public SearchingDevicesParameters(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
