package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

import android.bluetooth.BluetoothDevice;

/**
 * The result of the pairing process.
 */
public class PairingResult {

    /**
     * The code returned by the pairing process.
     */
    private final int returnCode;

    /**
     * The bluetooth device paired.
     */
    private final BluetoothDevice bluetoothDevice;

    /**
     * Constructor.
     *
     * @param returnCode      The code returned by the pairing process.
     * @param bluetoothDevice The bluetooth device paired.
     */
    PairingResult(int returnCode, BluetoothDevice bluetoothDevice) {
        this.returnCode = returnCode;
        this.bluetoothDevice = bluetoothDevice;
    }

    /**
     * Returns the code returned by the pairing process.
     *
     * @return The code returned by the pairing process.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the bluetooth device paired.
     *
     * @return The bluetooth device paired.
     */
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
