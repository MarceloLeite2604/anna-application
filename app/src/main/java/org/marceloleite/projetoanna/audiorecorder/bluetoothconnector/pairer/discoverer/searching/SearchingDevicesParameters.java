package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching;

import android.content.Context;

/**
 * Specifies the parameters required to construct a {@link AlertDialogSearchingDevices} object.
 */
public class SearchingDevicesParameters {

    /**
     * The context of the application in execution.
     */
    private final Context context;

    /**
     * Constructor.
     *
     * @param context The context of the application in execution.
     */
    public SearchingDevicesParameters(Context context) {
        this.context = context;
    }

    /**
     * Returns the context of the application in execution.
     *
     * @return The context of the application in execution.
     */
    public Context getContext() {
        return context;
    }
}
