package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice;

import android.bluetooth.BluetoothDevice;

/**
 * Created by marcelo on 26/06/17.
 */

public class SelectBluetoothDeviceResult {

    private int returnCode;

    private BluetoothDevice bluetoothDevice;

    public SelectBluetoothDeviceResult(int returnCode, BluetoothDevice bluetoothDevice) {
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
