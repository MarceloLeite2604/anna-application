package org.marceloleite.projetoanna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.marceloleite.projetoanna.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.bluetooth.ConnectInterface;
import org.marceloleite.projetoanna.bluetooth.ConnectThread;

import java.util.Set;

/**
 * Created by Marcelo Leite on 17/03/2017.
 */

public class ButtonConnectOnClickListener implements View.OnClickListener, ConnectInterface {

    private static final String TAG = ButtonConnectOnClickListener.class.toString();

    private AppCompatActivity appCompatActivity;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice bluetoothDevice;

    private BluetoothSocket bluetoothSocket;

    private ConnectThread bluetoothConnectThread;

    public ButtonConnectOnClickListener(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        this.bluetoothConnectThread = new ConnectThread(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onClick(View view) {
        ConnectToBluetoothServer();
    }

    public void ConnectToBluetoothServer() {

        if (bluetoothAdapter == null) {
            Log.e(TAG, "ConnectToBluetoothServer: This device does not have a bluetooth connector.");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            appCompatActivity.startActivityForResult(enableBluetoothIntent, Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE);
            return;
        }

        Set<BluetoothDevice> pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();

        if (pairedBluetoothDevices.size() > 0) {
            /* TODO: Show paired devices to user select one. */
        }

        bluetoothConnectThread.run();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public void returnBluetoothSocket(BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket != null) {
            this.bluetoothSocket = bluetoothSocket;
        }
    }
}
