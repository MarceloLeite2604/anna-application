package org.marceloleite.projetoanna.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by marcelo on 18/03/17.
 */

public interface ConnectInterface {

    BluetoothDevice getBluetoothDevice();

    void returnBluetoothSocket(BluetoothSocket bluetoothSocket);
}
