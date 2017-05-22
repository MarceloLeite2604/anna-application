package org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcelo Leite on 19/03/2017.
 */

public interface SelectBluetoothDeviceInterface {

    AppCompatActivity getAppCompatActivity();

    void bluetoothDeviceSelected(BluetoothDevice bluetoothDevice);
}
