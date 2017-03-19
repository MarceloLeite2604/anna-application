package org.marceloleite.projetoanna.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by Marcelo Leite on 03/05/2016.
 */
public class AlertDialogSelectDevice extends AlertDialog implements View.OnClickListener {

    private LinearLayout linearLayoutDevices;
    private SelectDeviceInterface selectDeviceInterface;

    public AlertDialogSelectDevice(SelectDeviceInterface selectDeviceInterface) {
        super(selectDeviceInterface.getAppCompatActivity());
        this.selectDeviceInterface = selectDeviceInterface;

        setIcon(0);
        setTitle("Select a device");
        linearLayoutDevices = new LinearLayout(getContext());
        linearLayoutDevices.setOrientation(LinearLayout.VERTICAL);
        for (BluetoothDevice bluetoothDevice : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            DeviceView deviceView = new DeviceView(getContext(), bluetoothDevice);
            deviceView.setOnClickListener(this);
            linearLayoutDevices.addView(deviceView);
        }
        setView(linearLayoutDevices);
        show();
    }

    @Override
    public void onClick(View v) {
        DeviceView deviceView = (DeviceView) v;
        dismiss();
        selectDeviceInterface.setBluetoothDevice(deviceView.getBluetoothDevice());
    }
}
