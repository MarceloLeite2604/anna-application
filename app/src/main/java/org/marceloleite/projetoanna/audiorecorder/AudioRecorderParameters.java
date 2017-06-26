package org.marceloleite.projetoanna.audiorecorder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcelo on 26/06/17.
 */

public class AudioRecorderParameters {

    private AppCompatActivity appCompatActivity;

    private BluetoothDevice bluetoothDevice;

    private BluetoothSocket bluetoothSocket;

    public AudioRecorderParameters(AppCompatActivity appCompatActivity, BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket) {
        this.appCompatActivity = appCompatActivity;
        this.bluetoothDevice = bluetoothDevice;
        this.bluetoothSocket = bluetoothSocket;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }
}
