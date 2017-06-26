package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector;

import android.bluetooth.BluetoothSocket;

/**
 * Created by marcelo on 26/06/17.
 */

public class ConnectWithAudioRecorderResult {

    private int returnCode;

    private BluetoothSocket bluetoothSocket;

    public ConnectWithAudioRecorderResult(int returnCode, BluetoothSocket bluetoothSocket) {
        this.returnCode = returnCode;
        this.bluetoothSocket = bluetoothSocket;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }
}
