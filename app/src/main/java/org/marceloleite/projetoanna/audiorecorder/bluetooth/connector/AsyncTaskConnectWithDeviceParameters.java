package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcelo on 18/03/17.
 */

public class AsyncTaskConnectWithDeviceParameters {

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
