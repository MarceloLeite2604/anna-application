package org.marceloleite.projetoanna.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.bluetooth.connector.AsyncTaskConnectToDevice;
import org.marceloleite.projetoanna.bluetooth.connector.AsyncTaskConnectToDeviceParameters;
import org.marceloleite.projetoanna.bluetooth.connector.AsyncTaskConnectToDeviceResponse;
import org.marceloleite.projetoanna.bluetooth.pairer.AlertDialogStartPairing;
import org.marceloleite.projetoanna.bluetooth.pairer.Pairer;

import java.util.Set;
import java.util.UUID;

/**
 * Created by marcelo on 18/03/17.
 */

public class Bluetooth implements SelectDeviceInterface, AsyncTaskConnectToDeviceParameters, AsyncTaskConnectToDeviceResponse {

    public static final UUID BLUETOOTH_SERVICE_UUID = UUID.fromString("f5934b96-0110-11e6-8d22-5e5517507c66");

    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 0x869a;

    private Pairer pairer;

    private MainActivity mainActivity;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice bluetoothDevice = null;

    private BluetoothSocket bluetoothSocket = null;

    private AsyncTaskConnectToDevice asyncTaskConnectToDevice;


    public Bluetooth(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void ConnectToBluetoothServer() {
        if (bluetoothAdapter == null) {
            Log.e(MainActivity.LOG_TAG, "ConnectToBluetoothServer, 38: This device does not have a bluetooth connector.");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(MainActivity.LOG_TAG, "ConnectToBluetoothServer, 48: Bluetooth adapter is not enabled.");
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(enableBluetoothIntent, Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE);
            return;
        }

        if (bluetoothDevice == null) {
            Log.d(MainActivity.LOG_TAG, "ConnectToBluetoothServer, 55: No device to connect.");
            Set<BluetoothDevice> pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();

            if (pairedBluetoothDevices.size() > 0) {
                new AlertDialogSelectBluetoothDevice(this).show();
                return;
            } else {
                new AlertDialogStartPairing(this).show();
                return;
            }
        }

        if (asyncTaskConnectToDevice == null) {
            Log.d(MainActivity.LOG_TAG, "ConnectToBluetoothServer, 77: Connecting to device " + bluetoothDevice.getAddress());
            asyncTaskConnectToDevice = new AsyncTaskConnectToDevice(this, this);
            asyncTaskConnectToDevice.execute();
        } else {
            Log.d(MainActivity.LOG_TAG, "ConnectToBluetoothServer, 86: Connection with device concluded.");
            asyncTaskConnectToDevice = null;
            if (bluetoothSocket == null) {
                Toast.makeText(mainActivity, "Connection with \"" + bluetoothDevice.getName() + "\" failed.", Toast.LENGTH_SHORT).show();
                mainActivity.disconnected();
            } else {
                Toast.makeText(mainActivity, "Connected with \"" + bluetoothDevice.getName() + "\".", Toast.LENGTH_SHORT).show();
                mainActivity.connectionStablished();

            }
        }
    }

    public void StartDeviceDiscover() {
        pairer = new Pairer(this);
        pairer.startDeviceDiscovery();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        ConnectToBluetoothServer();
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return mainActivity;
    }

    @Override
    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        Log.d(MainActivity.LOG_TAG, "setBluetoothDevice, 93: Bluetooth device is " + bluetoothDevice.getAddress());
        ConnectToBluetoothServer();
    }

    @Override
    public void connectToDeviceProcessFinished(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        ConnectToBluetoothServer();
    }

    public static void fillBluetoothDeviceInformations(View view, BluetoothDevice bluetoothDevice) {
        TextView textViewBluetoothDeviceName = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_name);
        TextView textViewBluetoothDeviceAddress = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_address);
        textViewBluetoothDeviceName.setText(bluetoothDevice.getName());
        textViewBluetoothDeviceAddress.setText(bluetoothDevice.getAddress());
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }
}