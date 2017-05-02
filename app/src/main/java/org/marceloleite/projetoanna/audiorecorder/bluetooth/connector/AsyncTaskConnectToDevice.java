package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;

import java.io.IOException;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class AsyncTaskConnectToDevice extends AsyncTask<Void, Integer, BluetoothSocket> {

    private static final String LOG_TAG = AsyncTaskConnectToDevice.class.getSimpleName();

    private AlertDialogConnectingToDevice alertDialogConnectingToDevice;

    private AsyncTaskConnectToDeviceParameters parameters;

    private AsyncTaskConnectToDeviceResponse response;

    private BluetoothSocket bluetoothSocket;

    public AsyncTaskConnectToDevice(AsyncTaskConnectToDeviceParameters asyncTaskConnectToDeviceParameters, AsyncTaskConnectToDeviceResponse asyncTaskConnectToDeviceResponse) {
        this.parameters = asyncTaskConnectToDeviceParameters;
        this.response = asyncTaskConnectToDeviceResponse;

        BluetoothSocket temporaryBluetoothSocket = null;

        try {
            temporaryBluetoothSocket = parameters.getBluetoothDevice().createRfcommSocketToServiceRecord(Bluetooth.BLUETOOTH_SERVICE_UUID);
        } catch (IOException createSocketException) {
            Log.e(LOG_TAG, "AsyncTaskConnectToDevice, 34: Could not create RFCOMM socket to service.", createSocketException);
        }
        bluetoothSocket = temporaryBluetoothSocket;
        Log.d(LOG_TAG, "AsyncTaskConnectToDevice, 39: " + bluetoothSocket.toString());
    }

    @Override
    protected void onPreExecute() {
        alertDialogConnectingToDevice = new AlertDialogConnectingToDevice(parameters.getAppCompatActivity(), parameters.getBluetoothDevice());
        alertDialogConnectingToDevice.show();
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        alertDialogConnectingToDevice.dismiss();
        response.connectToDeviceProcessFinished(bluetoothSocket);
    }

    @Override
    protected BluetoothSocket doInBackground(Void... voids) {
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            bluetoothSocket.connect();
        } catch (IOException connectException) {
            Log.e(LOG_TAG, "doInBackground, 42: Could not connect to device \"" + parameters.getBluetoothDevice().getAddress() + "\".", connectException);

            // Unable to connect; close the socket and return.
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.e(LOG_TAG, "doInBackground, 50: Could not close socket with device \"" + parameters.getBluetoothDevice().getAddress() + "\".");
            }
            bluetoothSocket = null;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        return bluetoothSocket;
    }
}
