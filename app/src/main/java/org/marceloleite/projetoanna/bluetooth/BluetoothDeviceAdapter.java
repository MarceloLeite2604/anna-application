package org.marceloleite.projetoanna.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.bluetooth.Bluetooth;

import java.util.ArrayList;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private ArrayList<BluetoothDevice> bluetoothArrayList;

    public BluetoothDeviceAdapter(Context context, int resource, ArrayList<BluetoothDevice> bluetoothArrayList) {
        super(context, resource, bluetoothArrayList);
        this.bluetoothArrayList = bluetoothArrayList;
    }

    @Override
    public int getCount() {
        return bluetoothArrayList.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return bluetoothArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View viewBluetoothDeviceRow = layoutInflater.inflate(R.layout.bluetooth_device_row, viewGroup, false);
        Bluetooth.fillBluetoothDeviceInformations(viewBluetoothDeviceRow, getItem(i));
        return viewBluetoothDeviceRow;
    }
}
