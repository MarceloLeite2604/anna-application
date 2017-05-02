package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcelo on 18/03/17.
 */

public interface AsyncTaskConnectToDeviceParameters {

    BluetoothDevice getBluetoothDevice();

    void setBluetoothSocket(BluetoothSocket bluetoothSocket);

    AppCompatActivity getAppCompatActivity();
}
