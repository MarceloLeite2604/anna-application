package org.marceloleite.projetoanna.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.bluetooth.pairer.Pairer;

import java.util.Set;
import java.util.UUID;

/**
 * Created by marcelo on 18/03/17.
 */

public class Bluetooth implements ConnectDeviceInterface, SelectDeviceInterface {

    public static final UUID BLUETOOTH_SERVICE_UUID = UUID.fromString("f5934b96-0110-11e6-8d22-5e5517507c66");

    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 0x869a;

    private Pairer pairer;

    private AppCompatActivity appCompatActivity;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice bluetoothDevice;

    private BluetoothSocket bluetoothSocket;

    public Bluetooth(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void ConnectToBluetoothServer() {
        if (bluetoothAdapter == null) {
            Log.e(MainActivity.LOG_TAG, "ConnectToBluetoothServer, 38: This device does not have a bluetooth connector.");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            appCompatActivity.startActivityForResult(enableBluetoothIntent, Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE);
            return;
        }

        if (bluetoothDevice == null) {
            Set<BluetoothDevice> pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();

            if (pairedBluetoothDevices.size() > 0) {
                new AlertDialogSelectDevice(this);
                return;
            } else {
                /* TODO */
                return;
            }
        }

        new ConnectThread(this).run();
    }

    public void StartDeviceDiscover() {
        pairer = new Pairer(appCompatActivity);
        pairer.startDeviceDiscovery();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    @Override
    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        ConnectToBluetoothServer();
    }
}
