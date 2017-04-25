package org.marceloleite.projetoanna.bluetooth.pairer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import org.marceloleite.projetoanna.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.ui.InformationView;
import org.marceloleite.projetoanna.MainActivity;

/**
 * Created by Marcelo Leite on 30/04/2016.
 */
public class Pairer {

    private static final String LOG_TAG = Pairer.class.getSimpleName();

    private Bluetooth bluetooth;

    private AlertDialogSearchingDevices alertDialogSearchingDevices;

    private AlertDialog alertDialogPairingDevice;

    private BroadcastReceiver broadcastReceiver;

    private BluetoothDevice bluetoothDevice;

    public Pairer(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
        alertDialogSearchingDevices = new AlertDialogSearchingDevices(bluetooth.getAppCompatActivity(), this);
        registerBroadcastReceiver();
    }

    public void startDeviceDiscovery() {
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    private void unregisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            bluetooth.getAppCompatActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void registerBroadcastReceiver() {
        if (broadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            broadcastReceiver = new BroadcastReceiver(this);
            bluetooth.getAppCompatActivity().registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    public void discoverDevicesStarted() {
        alertDialogSearchingDevices.discoveryDevicesStarted();
    }

    public void deviceFound(BluetoothDevice bluetoothDevice) {
        alertDialogSearchingDevices.addBluetoothDeviceToList(bluetoothDevice);
    }

    public void discoverDevicesFinished() {
        alertDialogSearchingDevices.discoveryDevicesFinished();
    }

    public void discoverDevicesConcluded(BluetoothDevice bluetoothDevice) {
        alertDialogSearchingDevices.dismiss();
        startPairing(bluetoothDevice);
    }

    public void discoverDevicesCancelled() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        unregisterBroadcastReceiver();
    }

    private void startPairing(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            this.bluetoothDevice = bluetoothDevice;
            this.bluetoothDevice.createBond();
        }
    }

    public void pairingStarted() {
        Log.i(LOG_TAG, "pairingStarted: Pairing started.");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(bluetooth.getAppCompatActivity());
        alertDialogBuilder.setTitle("Pairing");
        alertDialogPairingDevice = alertDialogBuilder.create();
        InformationView informationView = new InformationView(alertDialogPairingDevice.getContext());
        informationView.setInformationText("Pairing with \"" + bluetoothDevice.getName() + "\".");
        alertDialogPairingDevice.setView(informationView);
        alertDialogPairingDevice.show();
    }

    public void pairingFinished(boolean devicePaired) {
        Log.d(LOG_TAG, "pairingFinished, 109: Pairing finished.");

        if (devicePaired) {
            alertDialogPairingDevice.dismiss();
            bluetooth.setBluetoothDevice(bluetoothDevice);
        } else {
            Toast.makeText(bluetooth.getAppCompatActivity(), "Pairing with device \"" + bluetoothDevice.getName() + "\" failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
