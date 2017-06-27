package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer;

import android.bluetooth.BluetoothDevice;

/**
 * The result received from the {@link Discoverer} object once the bluetooth device discovering has concluded.
 */
public class DiscoveringResult {

    /**
     * The code returned from the bluetooth device discovering.
     */
    private final int returnCode;

    /**
     * The bluetooth device select by the user to pair with.
     */
    private final BluetoothDevice bluetoothDevice;

    /**
     * Constructor.
     *
     * @param returnCode      The code returned from the bluetooth device discovering.
     * @param bluetoothDevice The bluetooth device select by the user to pair with.
     */
    DiscoveringResult(int returnCode, BluetoothDevice bluetoothDevice) {
        this.returnCode = returnCode;
        this.bluetoothDevice = bluetoothDevice;
    }

    /**
     * Returns the code returned from the bluetooth device discovering.
     *
     * @return The code returned from the bluetooth device discovering.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the bluetooth device select by the user to pair with.
     *
     * @return The bluetooth device select by the user to pair with.
     */
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
