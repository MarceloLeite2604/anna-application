package org.marceloleite.projetoanna.bluetooth.pairer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.bluetooth.BluetoothDeviceAdapter;

import java.util.ArrayList;

/**
 * Created by Marcelo Leite on 30/04/2016.
 */
public class AlertDialogSearchingDevices extends AlertDialog implements View.OnClickListener, DialogInterface.OnClickListener, AdapterView.OnItemClickListener {

    private Pairer pairer;

    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;

    private BluetoothDeviceAdapter bluetoothDeviceAdapter;

    private ProgressBar progressBar;

    private TextView textViewInformation;

    public AlertDialogSearchingDevices(AppCompatActivity appCompatActivity, Pairer pairer) {
        super(appCompatActivity);
        this.pairer = pairer;

        setIcon(0);
        setTitle("Pairing");
        setButton(BUTTON_NEGATIVE, "Cancel", this);
        setButton(AlertDialog.BUTTON_POSITIVE, "Search again", this);

        LayoutInflater layoutInflater = appCompatActivity.getLayoutInflater();
        View contentView = layoutInflater.inflate(R.layout.bluetooth_device_pair, null);
        progressBar = (ProgressBar) contentView.findViewById(R.id.bluetooth_device_pair_progress_bar);
        textViewInformation = (TextView) contentView.findViewById(R.id.bluetooth_device_pair_text_view);
        setView(contentView);

        ListView bluetoothDevicesListView = (ListView) contentView.findViewById(R.id.list_view_bluetooth_devices);

        bluetoothDeviceArrayList = new ArrayList<>();

        bluetoothDeviceAdapter = new BluetoothDeviceAdapter(appCompatActivity, R.layout.bluetooth_device_row, bluetoothDeviceArrayList);

        bluetoothDevicesListView.setAdapter(bluetoothDeviceAdapter);
        bluetoothDevicesListView.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeViewMode(ViewMode.SEARCH_STARTED);
    }

    public void addBluetoothDeviceToList(BluetoothDevice bluetoothDevice) {
        bluetoothDeviceArrayList.add(bluetoothDevice);
        bluetoothDeviceAdapter.notifyDataSetChanged();
    }

    public void discoveryDevicesStarted() {
        if (!isShowing()) {
            show();
        }
        changeViewMode(ViewMode.SEARCH_STARTED);
    }

    public void discoveryDevicesFinished() {
        changeViewMode(ViewMode.SEARCH_FINISHED);
    }

    private void changeViewMode(ViewMode viewMode) {
        switch (viewMode) {
            case SEARCH_STARTED:

                getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                textViewInformation.setText("Searching devices...");

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
                pairer.startDeviceDiscovery();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                pairer.discoverDevicesCancelled();
                break;
            default:
                Log.e(MainActivity.LOG_TAG, "onClick, 118: Unknown button.");
                break;
        }
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BluetoothDevice bluetoothDevice = bluetoothDeviceArrayList.get(i);
        pairer.discoverDevicesConcluded(bluetoothDevice);
    }

    @Override
    public void onClick(View view) {
    }

    private enum ViewMode {
        SEARCH_STARTED,
        SEARCH_FINISHED
    }
}
