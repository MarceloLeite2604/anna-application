package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class AlertDialogConnectingToDevice extends AlertDialog {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogConnectingToDevice.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(AlertDialogConnectingToDevice.class);
    }

    public AlertDialogConnectingToDevice(AppCompatActivity appCompatActivity, BluetoothDevice bluetoothDevice) {
        super(appCompatActivity);
        setTitle("Connecting");
        setCancelable(true);
        LayoutInflater layoutInflater = appCompatActivity.getLayoutInflater();
        View bluetoothDeviceInfoView = layoutInflater.inflate(R.layout.bluetooth_device_connect, null);
        Bluetooth.fillBluetoothDeviceInformations(bluetoothDeviceInfoView, bluetoothDevice);
        setView(bluetoothDeviceInfoView);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        Log.d(AlertDialogConnectingToDevice.class, LOG_TAG, "setOnCancelListener (41): Cancelled.");
        super.setOnCancelListener(listener);
    }
}
