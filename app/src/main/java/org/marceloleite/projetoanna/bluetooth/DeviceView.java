package org.marceloleite.projetoanna.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;

/**
 * Created by Marcelo Leite on 03/05/2016.
 */
public class DeviceView extends LinearLayout {

    private BluetoothDevice bluetoothDevice;
    private TextView textViewDeviceName;

    public DeviceView(Context context, BluetoothDevice bluetoothDevice) {
        super(context);
        if (context == null){
            throw new IllegalArgumentException("Context cannot be null.");
        }
        if (bluetoothDevice == null){
            throw new IllegalArgumentException("BluetoothDevice cannot be null.");
        }
        this.bluetoothDevice = bluetoothDevice;
        View.inflate(context, R.layout.device_item, this);
        textViewDeviceName = (TextView)findViewById(R.id.device_item_name);
        textViewDeviceName.setText(bluetoothDevice.getName());
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
