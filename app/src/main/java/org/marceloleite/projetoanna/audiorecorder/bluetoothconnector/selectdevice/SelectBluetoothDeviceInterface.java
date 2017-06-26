package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice;

import android.bluetooth.BluetoothDevice;

/**
 * Defines the parameters required to select a bluetooth device and the method to be executed after a bluetooth device is selected.
 */
public interface SelectBluetoothDeviceInterface {

    /**
     * Returns the parameters to show the alert dialog for user to select the bluetooth device to connect.
     *
     * @return The parameters to show the alert dialog for user to select the bluetooth device to connect.
     */
    SelectBluetoothDeviceParameters getSelectBluetoothDeviceParameters();

    void bluetoothDeviceSelected(SelectBluetoothDeviceResult selectBluetoothDeviceResult);
}
