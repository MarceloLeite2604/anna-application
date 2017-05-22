package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public interface AsyncTaskConnectWithDeviceResult {

    void connectWithDeviceProcessFinished(BluetoothSocket bluetoothSocket);
}
