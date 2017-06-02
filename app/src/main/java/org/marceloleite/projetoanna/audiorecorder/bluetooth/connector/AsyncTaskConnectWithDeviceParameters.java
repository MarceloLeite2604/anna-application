package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by marcelo on 18/03/17.
 */

public class AsyncTaskConnectWithDeviceParameters {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AsyncTaskConnectWithDeviceParameters.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(AsyncTaskConnectWithDeviceParameters.class);
    }

    private BluetoothDevice bluetoothDevice;

    private AppCompatActivity appCompatActivity;

    public AsyncTaskConnectWithDeviceParameters(BluetoothDevice bluetoothDevice, AppCompatActivity appCompatActivity) {
        this.bluetoothDevice = bluetoothDevice;
        this.appCompatActivity = appCompatActivity;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }
}
