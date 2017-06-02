package org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.BluetoothDeviceAdapter;
import org.marceloleite.projetoanna.utils.Log;

import java.util.ArrayList;


/**
 * Created by Marcelo Leite on 03/05/2016.
 */
public class AlertDialogSelectBluetoothDevice extends AlertDialog implements AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogSelectBluetoothDevice.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(AlertDialogSelectBluetoothDevice.class);
    }

    private SelectBluetoothDeviceInterface selectBluetoothDeviceInterface;
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;

    private BluetoothDevice bluetoothDeviceSelected;

    public AlertDialogSelectBluetoothDevice(SelectBluetoothDeviceInterface selectBluetoothDeviceInterface) {
        super(selectBluetoothDeviceInterface.getAppCompatActivity());
        this.selectBluetoothDeviceInterface = selectBluetoothDeviceInterface;
        setIcon(0);
        setTitle("Select a device");
        LayoutInflater layoutInflater = selectBluetoothDeviceInterface.getAppCompatActivity().getLayoutInflater();
        View convertView = layoutInflater.inflate(R.layout.bluetooth_devices_list, null);
        setView(convertView);
        ListView bluetoothDevicesListView = (ListView) convertView.findViewById(R.id.list_view_bluetooth_devices);

        bluetoothDeviceArrayList = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            bluetoothDeviceArrayList.add(bluetoothDevice);
        }

        bluetoothDevicesListView.setAdapter(new BluetoothDeviceAdapter(selectBluetoothDeviceInterface.getAppCompatActivity(), R.layout.bluetooth_device_row, bluetoothDeviceArrayList));
        bluetoothDevicesListView.setOnItemClickListener(this);
        setOnDismissListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bluetoothDeviceSelected = bluetoothDeviceArrayList.get(i);
        hide();
        dismiss();
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        selectBluetoothDeviceInterface.bluetoothDeviceSelected(this.bluetoothDeviceSelected);
    }
}
