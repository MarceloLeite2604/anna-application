package org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;

/**
 * Defines the parameters required to select a bluetooth device and the method to be executed after a bluetooth device is selected.
 */
public interface SelectBluetoothDeviceInterface {

    AppCompatActivity getAppCompatActivity();

    /**
     * The method to be executed after a bluetooth device is selected.
     *
     * @param bluetoothDevice
     */
    void bluetoothDeviceSelected(BluetoothDevice bluetoothDevice);
}
