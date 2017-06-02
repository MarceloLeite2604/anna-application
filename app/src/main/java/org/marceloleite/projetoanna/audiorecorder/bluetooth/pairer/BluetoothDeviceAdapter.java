package org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.utils.Log;

import java.util.ArrayList;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = BluetoothDeviceAdapter.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(BluetoothDeviceAdapter.class);
    }

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
