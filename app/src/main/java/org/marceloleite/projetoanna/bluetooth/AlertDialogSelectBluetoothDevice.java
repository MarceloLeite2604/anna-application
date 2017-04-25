package org.marceloleite.projetoanna.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.bluetooth.BluetoothDeviceAdapter;
import org.marceloleite.projetoanna.bluetooth.SelectDeviceInterface;

import java.util.ArrayList;


/**
 * Created by Marcelo Leite on 03/05/2016.
 */
public class AlertDialogSelectBluetoothDevice extends AlertDialog implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = AlertDialogSelectBluetoothDevice.class.getSimpleName();

    private SelectDeviceInterface selectDeviceInterface;
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;

    public AlertDialogSelectBluetoothDevice(SelectDeviceInterface selectDeviceInterface) {
        super(selectDeviceInterface.getAppCompatActivity());
        this.selectDeviceInterface = selectDeviceInterface;
        setIcon(0);
        setTitle("Select a device");
        LayoutInflater layoutInflater = selectDeviceInterface.getAppCompatActivity().getLayoutInflater();
        View convertView = layoutInflater.inflate(R.layout.bluetooth_devices_list, null);
        setView(convertView);
        ListView bluetoothDevicesListView = (ListView) convertView.findViewById(R.id.list_view_bluetooth_devices);

        bluetoothDeviceArrayList = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            bluetoothDeviceArrayList.add(bluetoothDevice);
        }

        bluetoothDevicesListView.setAdapter(new BluetoothDeviceAdapter(selectDeviceInterface.getAppCompatActivity(), R.layout.bluetooth_device_row, bluetoothDeviceArrayList));
        bluetoothDevicesListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(LOG_TAG, "onItemClick, 51: Item selected.");
        hide();
        dismiss();
        selectDeviceInterface.setBluetoothDevice(bluetoothDeviceArrayList.get(i));
    }
}
