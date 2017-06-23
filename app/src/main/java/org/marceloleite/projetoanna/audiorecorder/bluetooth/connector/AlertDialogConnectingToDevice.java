package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.utils.Log;

/**
 * An alert dialog box to inform the user the application is connecting to a bluetooth device.
 */

class AlertDialogConnectingToDevice extends AlertDialog {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogConnectingToDevice.class.getSimpleName();

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
     * @param rootViewGroup     The root view group of this alert dialog.
     */
    AlertDialogConnectingToDevice(AppCompatActivity appCompatActivity, BluetoothDevice bluetoothDevice, ViewGroup rootViewGroup) {
        super(appCompatActivity);
        setTitle("Connecting");
        setCancelable(true);
        LayoutInflater layoutInflater = appCompatActivity.getLayoutInflater();
        View bluetoothDeviceInfoView = layoutInflater.inflate(R.layout.bluetooth_device_connect, rootViewGroup);
        Bluetooth.fillBluetoothDeviceInformations(bluetoothDeviceInfoView, bluetoothDevice);
        setView(bluetoothDeviceInfoView);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        Log.d(LOG_TAG, "setOnCancelListener (41): Connection cancelled.");
        super.setOnCancelListener(listener);
    }
}
