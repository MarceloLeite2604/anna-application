package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

import android.view.View;

/**
 * A view holder for bluetooth device information shown.
 */
class BluetoothDeviceViewHolder {

    /**
     * The view which contains the bluetooth device information.
     */
    private final View bluetoothDeviceView;

    /**
     * Constructor.
     *
     * @param bluetoothDeviceView The view which contains the bluetooth device information.
     */
    BluetoothDeviceViewHolder(View bluetoothDeviceView) {
        this.bluetoothDeviceView = bluetoothDeviceView;
    }

    /**
     * Returns the view which contains the bluetooth device information.
     *
     * @return The view which contains the bluetooth device information.
     */
    View getBluetoothDeviceView() {
        return bluetoothDeviceView;
    }
}
