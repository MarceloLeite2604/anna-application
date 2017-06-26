package org.marceloleite.projetoanna.utils.bluetoothdevice;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;

/**
 * Created by marcelo on 26/06/17.
 */

public abstract class BluetoothDeviceUtils {
    /**
     * Fills a bluetooth device view with its information.
     *
     * @param view            The bluetooth device view to be filled.
     * @param bluetoothDevice The bluetooth device which the information must be shown on view.
     */
    public static void fillBluetoothDeviceInformation(View view, BluetoothDevice bluetoothDevice) {
        TextView textViewBluetoothDeviceName = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_name);
        TextView textViewBluetoothDeviceAddress = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_address);
        textViewBluetoothDeviceName.setText(bluetoothDevice.getName());
        textViewBluetoothDeviceAddress.setText(bluetoothDevice.getAddress());
    }
}
