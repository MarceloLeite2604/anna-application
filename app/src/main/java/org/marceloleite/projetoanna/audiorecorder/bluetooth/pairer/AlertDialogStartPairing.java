package org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 30/04/2016.
 */
public class AlertDialogStartPairing extends AlertDialog implements DialogInterface.OnClickListener {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogStartPairing.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(AlertDialogStartPairing.class);
    }

    private Bluetooth bluetooth;

    public AlertDialogStartPairing(Bluetooth bluetooth) {
        super(bluetooth.getAppCompatActivity());
        this.bluetooth = bluetooth;
        setMessage("No devices paired yet. Start discovering Bluetooth devices?");
        setButton(BUTTON_POSITIVE, "Yes", this);
        setButton(BUTTON_NEGATIVE, "No", this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == DialogInterface.BUTTON_POSITIVE) {
            bluetooth.startDeviceDiscover();
        }

        dismiss();
    }
}
