package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

import android.bluetooth.BluetoothDevice;

/**
 * Created by marcelo on 26/06/17.
 */

public class PairingResult {

    private int returnCode;

    private BluetoothDevice bluetoothDevice;

    public PairingResult(int returnCode, BluetoothDevice bluetoothDevice) {
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
