package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.BluetoothDeviceAdapter;
import org.marceloleite.projetoanna.utils.Log;

import java.util.ArrayList;

/**
 * An alert dialog showing the list of equipments found while the searching for visible bluetooth
 * devices is being executed.
 */
public class AlertDialogSearchingDevices extends AlertDialog implements View.OnClickListener, DialogInterface.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogSearchingDevices.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * A list with all bluetooth devices discovered.
     */
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;

    /**
     * An adapter to fill the equipment's list while searching for bluetooth devices.
     */
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;

    /**
     * The progress bar shown on the alert dialog while scanning for visible bluetooth devices.
     */
    private ProgressBar progressBar;

    /**
     * The text view shown on the alert log with information about the scanning process.
     */
    private TextView textViewInformation;

    private SearchingDevicesInterface searchingDevicesInterface;

    public AlertDialogSearchingDevices(SearchingDevicesInterface searchingDevicesInterface) {
        super(searchingDevicesInterface.getSearchingDevicesParameters().getContext());
        this.searchingDevicesInterface = searchingDevicesInterface;
        SearchingDevicesParameters searchingDevicesParameters = searchingDevicesInterface.getSearchingDevicesParameters();

        setIcon(0);
        setTitle("Pairing");
        setButton(BUTTON_NEGATIVE, "Cancel", this);
        setButton(AlertDialog.BUTTON_POSITIVE, "Search again", this);

        View contentView = View.inflate(searchingDevicesParameters.getContext(), R.layout.bluetooth_device_pair, null);
        progressBar = contentView.findViewById(R.id.bluetooth_device_pair_progress_bar);
        textViewInformation = contentView.findViewById(R.id.bluetooth_device_pair_text_view);
        setView(contentView);

        ListView bluetoothDevicesListView = contentView.findViewById(R.id.list_view_bluetooth_devices);

        bluetoothDeviceArrayList = new ArrayList<>();

        bluetoothDeviceAdapter = new BluetoothDeviceAdapter(searchingDevicesParameters.getContext(), bluetoothDeviceArrayList);

        bluetoothDevicesListView.setAdapter(bluetoothDeviceAdapter);
        bluetoothDevicesListView.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeViewMode(ViewMode.SEARCH_STARTED);
    }

    public void discoveryStarted() {
        if (!isShowing()) {
            show();
        }
        changeViewMode(ViewMode.SEARCH_STARTED);
    }

    public void discoveryFinished() {
        changeViewMode(ViewMode.SEARCH_FINISHED);
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        bluetoothDeviceArrayList.add(bluetoothDevice);
        bluetoothDeviceAdapter.notifyDataSetChanged();
    }

    private void changeViewMode(ViewMode viewMode) {
        switch (viewMode) {
            case SEARCH_STARTED:

                getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                textViewInformation.setText(R.string.text_view_searching_devices_information_first_text);

                bluetoothDeviceArrayList.clear();
                bluetoothDeviceAdapter.notifyDataSetChanged();
                break;
            case SEARCH_FINISHED:
                getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                String information;

                if (bluetoothDeviceArrayList.size() > 0) {
                    information = "Select a device to pair.";
                } else {
                    information = "No devices found.";
                }

                textViewInformation.setText(information);
                break;
        }
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                searchingDevicesInterface.startDeviceDiscover();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                searchingDevicesInterface.cancelDeviceDiscover();
                break;
            default:
                Log.e(LOG_TAG, "onClick (134): Unknown button.");
                throw new RuntimeException("Unknown button pressed.");
        }
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BluetoothDevice bluetoothDevice = bluetoothDeviceArrayList.get(i);
        searchingDevicesInterface.deviceSelected(bluetoothDevice);
    }

    @Override
    public void onClick(View view) {
    }

    private enum ViewMode {
        SEARCH_STARTED,
        SEARCH_FINISHED
    }
}
