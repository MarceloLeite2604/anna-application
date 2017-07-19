package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector;

/**
 * Defines the parameters used to execute a bluetooth connection attempt and the method to be executed after the attempt.
 */
public interface BluetoothConnectorInterface {

    /**
     * Checks the result received from the bluetooth connection attempt.
     *
     * @param bluetoothConnectorResult The result received from the bluetooth connection attempt.
     */
    void bluetoothConnectionResult(BluetoothConnectorResult bluetoothConnectorResult);


}
