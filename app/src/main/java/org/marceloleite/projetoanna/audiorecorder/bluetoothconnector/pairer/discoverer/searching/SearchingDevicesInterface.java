package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching;

import android.bluetooth.BluetoothDevice;

/**
 * Specifies the methods required by the {@link AlertDialogSearchingDevices} object to inform the
 * user actions on the alert dialog.
 */
public interface SearchingDevicesInterface {

    /**
     * Returns the parameters informed to construct the {@link AlertDialogSearchingDevices} object.
     *
     * @return The parameters informed to construct the {@link AlertDialogSearchingDevices} object.
     */
    SearchingDevicesParameters getSearchingDevicesParameters();

    /**
     * Method executed when the user requested to start the bluetooth device discover.
     */
    void startDeviceDiscover();

    /**
     * Method executed when the user requested to cancel the bluetooth device discover.
     */
    void cancelDeviceDiscover();

    /**
     * Method executed when the user selected a bluetooth device to pair with.
     */
    void deviceSelected(BluetoothDevice bluetoothDeviceSelected);
}
