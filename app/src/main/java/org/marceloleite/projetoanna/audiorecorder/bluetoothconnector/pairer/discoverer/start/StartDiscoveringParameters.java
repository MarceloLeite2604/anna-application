package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.start;

import android.content.Context;

/**
 * The parameters required to construct a {@link AlertDialogStartDiscovering} object.
 */
public class StartDiscoveringParameters {

    /**
     * The context of the application in execution.
     */
    private final Context context;

    /**
     * Constructor.
     *
     * @param context The context of the application in execution.
     */
    public StartDiscoveringParameters(Context context) {
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
