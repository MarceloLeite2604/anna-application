package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothSocket;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public interface AsyncTaskConnectToDeviceResponse {

    void connectToDeviceProcessFinished(BluetoothSocket bluetoothSocket);
}
