package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.BluetoothDeviceAdapter;
import org.marceloleite.projetoanna.utils.Log;

import java.util.ArrayList;


/**
 * An alert dialog for user to select the bluetooth device to connect.
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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Contains the parameters required to show the alert dialog and the method to be executed after user has selected the device to connect.
     */
    private SelectBluetoothDeviceInterface selectBluetoothDeviceInterface;

    /**
     * A list to bluetooth devices shown by the alert dialog.
     */
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;

    /**
     * The bluetooth device selected to connect.
     */
    private BluetoothDevice bluetoothDeviceSelected;

    /**
     * Constructor.
     *
     * @param selectBluetoothDeviceInterface Contains the parameters required to show the alert dialog and the method to be executed after user has selected the device to connect.
     */
    public AlertDialogSelectBluetoothDevice(SelectBluetoothDeviceInterface selectBluetoothDeviceInterface) {
        super(selectBluetoothDeviceInterface.getSelectBluetoothDeviceParameters().getContext());
        this.selectBluetoothDeviceInterface = selectBluetoothDeviceInterface;
        setIcon(0);
        setTitle("Select a device");
        SelectBluetoothDeviceParameters selectBluetoothDeviceParameters = selectBluetoothDeviceInterface.getSelectBluetoothDeviceParameters();
        View convertView = View.inflate(selectBluetoothDeviceParameters.getContext(), R.layout.bluetooth_devices_list, null);
        setView(convertView);
        ListView bluetoothDevicesListView = (ListView) convertView.findViewById(R.id.list_view_bluetooth_devices);

        bluetoothDeviceArrayList = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            bluetoothDeviceArrayList.add(bluetoothDevice);
        }

        bluetoothDevicesListView.setAdapter(new BluetoothDeviceAdapter(selectBluetoothDeviceParameters.getContext(), R.layout.bluetooth_device_row, bluetoothDeviceArrayList));
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
        SelectBluetoothDeviceResult selectBluetoothDeviceResult;
        if (bluetoothDeviceSelected != null) {
            selectBluetoothDeviceResult = new SelectBluetoothDeviceResult(BluetoothConnectorReturnCodes.SUCCESS, bluetoothDeviceSelected);
        } else {
            selectBluetoothDeviceResult = new SelectBluetoothDeviceResult(BluetoothConnectorReturnCodes.DEVICE_SELECTION_CANCELLED, null);
        }
        selectBluetoothDeviceInterface.bluetoothDeviceSelected(selectBluetoothDeviceResult);
    }
}
