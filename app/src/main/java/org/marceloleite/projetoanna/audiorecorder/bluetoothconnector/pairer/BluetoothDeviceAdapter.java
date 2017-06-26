package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.bluetoothdevice.BluetoothDeviceUtils;

import java.util.ArrayList;

/**
 * An {@link ArrayAdapter} to show a list of equipments discovered while searching for bluetooth devices.
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
        Log.addClassToLog(LOG_TAG);
    }

    private ArrayList<BluetoothDevice> bluetoothArrayList;

    /**
     * Constructor
     *
     * @param context            The application context which requested the
     * @param resource           The layout which should be used to show the bluetooth device's information.
     * @param bluetoothArrayList The array list which contains all the bluetooth devices discovered.
     */
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /* TODO: Must check the execution after change the code to use a view holder. */
        BluetoothDeviceViewHolder bluetoothDeviceViewHolder;

        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.bluetooth_device_row, parent, false);

            bluetoothDeviceViewHolder = new BluetoothDeviceViewHolder(convertView);
            convertView.setTag(bluetoothDeviceViewHolder);
        } else {
            bluetoothDeviceViewHolder = (BluetoothDeviceViewHolder) convertView.getTag();
        }

        BluetoothDeviceUtils.fillBluetoothDeviceInformation(bluetoothDeviceViewHolder.getBluetoothDeviceView(), getItem(position));
        return convertView;
    }
}
