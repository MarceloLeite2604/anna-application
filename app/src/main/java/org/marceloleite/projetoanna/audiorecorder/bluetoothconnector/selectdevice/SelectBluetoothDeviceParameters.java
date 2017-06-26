package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * The parameters required to show the alert dialog for user to select the bluetooth device to connect.
 */
public class SelectBluetoothDeviceParameters {

    /**
     * The context of the activity which requested the user to select a bluetooth device.
     */
    private Context context;

    public SelectBluetoothDeviceParameters(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
