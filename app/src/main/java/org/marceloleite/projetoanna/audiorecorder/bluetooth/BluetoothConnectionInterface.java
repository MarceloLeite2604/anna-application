package org.marceloleite.projetoanna.audiorecorder.bluetooth;

import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver.BluetoothConnectionParameters;

/**
 * Defines the parameters used to execute a bluetooth connection attempt and the method to be executed after the attempt.
 */
public interface BluetoothConnectionInterface {

    /**
     * Checks the result received from the bluetooth connection attempt.
     *
     * @param result The result received from the bluetooth connection attempt.
     */
    void bluetoothConnectionResult(int result);

    /**
     * Returns the parameters required to execute the bluetooth connection attempt.
     *
     * @return The parameters required to execute the bluetooth connection attempt.
     */
    BluetoothConnectionParameters getBluetoothConnectionParameters();
}
