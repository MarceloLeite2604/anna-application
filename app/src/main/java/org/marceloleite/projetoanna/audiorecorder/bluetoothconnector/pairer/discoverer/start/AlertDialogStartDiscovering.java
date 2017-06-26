package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.start;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.utils.Log;

/**
 * An alert dialog shown to user to confirm the bluetooth device discovery.
 */
public class AlertDialogStartDiscovering extends AlertDialog implements DialogInterface.OnClickListener {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogStartDiscovering.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private StartDiscoveringInterface startDiscoveringInterface;

    public AlertDialogStartDiscovering(StartDiscoveringInterface startDiscoveringInterface) {
        super(startDiscoveringInterface.getStartDiscoveryParameters().getContext());
        this.startDiscoveringInterface = startDiscoveringInterface;
        setMessage(this.getContext().getString(R.string.alert_dialog_title_start_search_devices));
        setButton(BUTTON_POSITIVE, this.getContext().getString(R.string.yes), this);
        setButton(BUTTON_NEGATIVE, this.getContext().getString(R.string.no), this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        dismiss();
        startDiscoveringInterface.startDiscoveryResult(which);
    }
}
