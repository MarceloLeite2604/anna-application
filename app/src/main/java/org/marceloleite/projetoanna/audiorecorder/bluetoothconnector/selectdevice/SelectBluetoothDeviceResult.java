package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice;

import android.bluetooth.BluetoothDevice;

/**
 * The result of the bluetooth device selection.
 */
public class SelectBluetoothDeviceResult {

    /**
     * The code returned from the bluetooth device selection.
     */
    private final int returnCode;

    /**
     * The bluetooth device selected.
     */
    private final BluetoothDevice bluetoothDevice;

    /**
     * Constructor.
     *
     * @param returnCode      The code returned from the bluetooth device selection.
     * @param bluetoothDevice The bluetooth device selected.
     */
    SelectBluetoothDeviceResult(int returnCode, BluetoothDevice bluetoothDevice) {
        this.returnCode = returnCode;
        this.bluetoothDevice = bluetoothDevice;
    }

    /**
     * Returns the code returned from the bluetooth device selection.
     *
     * @return The code returned from the bluetooth device selection.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Retursn the bluetooth device selected.
     *
     * @return The bluetooth device selected.
     */
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
