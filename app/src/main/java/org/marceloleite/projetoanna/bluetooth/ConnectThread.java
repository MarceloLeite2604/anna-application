package org.marceloleite.projetoanna.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * Created by marcelo on 18/03/17.
 */

public class ConnectThread extends Thread {

    private static final String TAG = ConnectThread.class.getName();

    private final ConnectInterface connectInterface;
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;

    public ConnectThread(ConnectInterface connectInterface) {
        this.connectInterface = connectInterface;

        bluetoothDevice = connectInterface.getBluetoothDevice();

        if (bluetoothDevice == null) {
            Log.e(TAG, "ConnectThread: No device to connect.");
        }

        BluetoothSocket temporaryBluetoothSocket = null;

        try {
            temporaryBluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(Bluetooth.BLUETOOTH_SERVICE_UUID);
        } catch (IOException e) {
            Log.e(TAG, "ConnectThread: Could not create RFCOMM socket to service.", e);
        }
        bluetoothSocket = temporaryBluetoothSocket;

    }

    public void run() {
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            bluetoothSocket.connect();
        } catch (IOException connectException) {
            Log.e(TAG, "Could not connect do device " + bluetoothDevice.getAddress() + ".", connectException);
            // Unable to connect; close the socket and return.
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the socket with device " + bluetoothDevice.getAddress(), closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        connectInterface.returnBluetoothSocket(bluetoothSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException closeException) {
            Log.e(TAG, "Could not close the client socket", closeException);
        }
    }
}
