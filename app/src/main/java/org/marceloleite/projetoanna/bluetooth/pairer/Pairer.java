package org.marceloleite.projetoanna.bluetooth.pairer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.marceloleite.projetoanna.InformationView;
import org.marceloleite.projetoanna.MainActivity;

/**
 * Created by Marcelo Leite on 30/04/2016.
 */
public class Pairer {

    private static final String TAG = Pairer.class.toString();

    AppCompatActivity appCompatActivity;

    BluetoothAdapter bluetoothAdapter;

    private AlertDialogSearchingDevices alertDialogSearchingDevices;

    private AlertDialog alertDialogPairingDevice;

    private BroadcastReceiver broadcastReceiver;

    private BluetoothDevice bluetoothDevice;

    public Pairer(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        alertDialogSearchingDevices = new AlertDialogSearchingDevices(this);
    }

    public void startDeviceDiscovery(){
        unregisterBroadcastReceiver();
        bluetoothAdapter.cancelDiscovery();
        registerBroadcastReceiver();
        bluetoothAdapter.startDiscovery();
    }

    private void unregisterBroadcastReceiver(){
        if (broadcastReceiver != null) {
            appCompatActivity.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void registerBroadcastReceiver(){
        if (broadcastReceiver == null ) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            broadcastReceiver = new BroadcastReceiver(this);
            appCompatActivity.registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    public void discoverDevicesStarted(){
        alertDialogSearchingDevices.discoveryDevicesStarted();
    }

    public void deviceFound(BluetoothDevice bluetoothDevice){
        alertDialogSearchingDevices.addBluetoothDeviceToList(bluetoothDevice);
    }

    public void discoverDevicesFinished(){
        alertDialogSearchingDevices.discoveryDevicesFinished();
    }

    public void discoverDevicesConcluded(BluetoothDevice bluetoothDevice){
        bluetoothAdapter.cancelDiscovery();
        alertDialogSearchingDevices.dismiss();

        if (bluetoothDevice != null) {
            startPairing(bluetoothDevice);
        }
    }

    private void startPairing(BluetoothDevice bluetoothDevice){
        this.bluetoothDevice = bluetoothDevice;
        this.bluetoothDevice.createBond();
    }

    public void pairingStarted() {
        Log.i(TAG, "pairingStarted: Pairing started.");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appCompatActivity);
        alertDialogBuilder.setTitle("Pairing");
        alertDialogPairingDevice = alertDialogBuilder.create();
        InformationView informationView = new InformationView(alertDialogPairingDevice.getContext());
        informationView.setInformationText("Pairing with \"" + bluetoothDevice.getName() + "\".");
        alertDialogPairingDevice.setView(informationView);
        alertDialogPairingDevice.show();
    }

    public void pairingFinished(boolean devicePaired) {
        Log.d(MainActivity.LOG_TAG, "pairingFinished, 109: Pairing finished.");
        alertDialogPairingDevice.dismiss();
        if (devicePaired) {
            /* TODO: Return bluetooth device. */
        }
    }
}
