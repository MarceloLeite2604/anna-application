package org.marceloleite.projetoanna.audiorecorder.communicator;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Created by marcelo on 26/06/17.
 */

public class CommunicatorParameters {

    private BluetoothSocket bluetoothSocket;

    private Context context;

    public CommunicatorParameters(BluetoothSocket bluetoothSocket, Context context) {
        this.bluetoothSocket = bluetoothSocket;
        this.context = context;
    }

    BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    Context getContext() {
        return context;
    }
}
