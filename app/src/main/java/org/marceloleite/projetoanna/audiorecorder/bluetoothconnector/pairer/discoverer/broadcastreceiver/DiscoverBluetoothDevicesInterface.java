package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.broadcastreceiver;

import android.bluetooth.BluetoothDevice;

/**
 * Establishes the methods used during the bluetooth device discovering process.
 */
public interface DiscoverBluetoothDevicesInterface {

    /**
     * Method executed after the bluetooth device discovering starts.
     */
    void discoverDevicesStarted();

    /**
     * Method after the bluetooth device discovering finishes.
     */
    void discoverDevicesFinished();

    /**
     * Method executed when a bluetooth device was found on discovering process.
     *
     * @param bluetoothDevice The bluetooth device found.
     */
    void deviceFound(BluetoothDevice bluetoothDevice);

}
