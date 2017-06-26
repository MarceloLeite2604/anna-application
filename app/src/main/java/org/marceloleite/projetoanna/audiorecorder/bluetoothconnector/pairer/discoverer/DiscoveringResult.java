package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer;

import android.bluetooth.BluetoothDevice;

/**
 * Created by marcelo on 26/06/17.
 */

public class DiscoveringResult {

    private int returnCode;

    private BluetoothDevice bluetoothDevice;

    DiscoveringResult(int returnCode, BluetoothDevice bluetoothDevice) {
        this.returnCode = returnCode;
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
