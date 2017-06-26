package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.bluetoothdevice.BluetoothDeviceUtils;

/**
 * An alert dialog box to inform the user the application is connecting to a bluetooth device.
 */

class AlertDialogConnectingWithAudioRecorder extends AlertDialog {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogConnectingWithAudioRecorder.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Object constructor.
     *
     * @param appCompatActivity The activity which requested the bluetooth connection.
     * @param bluetoothDevice   The bluetooth device which the application is connecting with.
     */
    AlertDialogConnectingWithAudioRecorder(AppCompatActivity appCompatActivity, BluetoothDevice bluetoothDevice) {
        super(appCompatActivity);
        setTitle("Connecting");
        setCancelable(true);
        View bluetoothDeviceInfoView = View.inflate(appCompatActivity, R.layout.bluetooth_device_connect, null);
        BluetoothDeviceUtils.fillBluetoothDeviceInformation(bluetoothDeviceInfoView, bluetoothDevice);
        setView(bluetoothDeviceInfoView);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        Log.d(LOG_TAG, "setOnCancelListener (41): Connection cancelled.");
        super.setOnCancelListener(listener);
    }
}
