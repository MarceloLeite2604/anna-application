package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothSocket;

/**
 * Implements the process to be executed after the bluetooth connection attempt is concluded.
 */
public interface AsyncTaskConnectWithDeviceResult {

    /**
     * Process to be executed after the bluetooth connection with a remove device is concluded.
     *
     * @param bluetoothSocket The bluetooth socket connection with the remove device.
     */
    void connectWithDeviceProcessFinished(BluetoothSocket bluetoothSocket);

    /**
     * Returns the parameters required to execute the bluetooth connection task.
     * @return The parameters required to execute the bluetooth connection task.
     */
    AsyncTaskConnectWithDeviceParameters getAsyncTaskConnectWithDeviceParameters();
}
