package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching;

import android.bluetooth.BluetoothDevice;

/**
 * Created by marcelo on 26/06/17.
 */

public interface SearchingDevicesInterface {

    SearchingDevicesParameters getSearchingDevicesParameters();

    void startDeviceDiscover();

    void cancelDeviceDiscover();

    void deviceSelected(BluetoothDevice bluetoothDeviceSelected);
}
