package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;

import java.io.IOException;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class AsyncTaskConnectWithDevice extends AsyncTask<AsyncTaskConnectWithDeviceParameters, Integer, BluetoothSocket> {

    private static final String LOG_TAG = AsyncTaskConnectWithDevice.class.getSimpleName();

    private AlertDialogConnectingToDevice alertDialogConnectingToDevice;

    private AsyncTaskConnectWithDeviceParameters parameters;

    private AsyncTaskConnectWithDeviceResult result;

    private BluetoothSocket bluetoothSocket;

    public AsyncTaskConnectWithDevice(AsyncTaskConnectWithDeviceParameters asyncTaskConnectWithDeviceParameters, AsyncTaskConnectWithDeviceResult asyncTaskConnectWithDeviceResult) {
        this.parameters = asyncTaskConnectWithDeviceParameters;
        this.result = asyncTaskConnectWithDeviceResult;


    }

    @Override
    protected void onPreExecute() {
        alertDialogConnectingToDevice = new AlertDialogConnectingToDevice(parameters.getAppCompatActivity(), parameters.getBluetoothDevice());
        alertDialogConnectingToDevice.show();
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        alertDialogConnectingToDevice.dismiss();
        result.connectWithDeviceProcessFinished(bluetoothSocket);
    }

    @Override
    protected BluetoothSocket doInBackground(AsyncTaskConnectWithDeviceParameters... asyncTaskConnectWithDeviceParameterses) {

        BluetoothSocket temporaryBluetoothSocket = null;

        try {
            temporaryBluetoothSocket = parameters.getBluetoothDevice().createRfcommSocketToServiceRecord(Bluetooth.BLUETOOTH_SERVICE_UUID);
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "AsyncTaskConnectWithDevice, 34: Could not create RFCOMM socket to service.", ioException);
        }
        bluetoothSocket = temporaryBluetoothSocket;
        Log.d(LOG_TAG, "AsyncTaskConnectWithDevice, 39: " + bluetoothSocket.toString());

        try {
            bluetoothSocket.connect();
        } catch (IOException connectException) {
            Log.w(LOG_TAG, "doInBackground, 42: Could not connectWithAudioRecorder to device \"" + parameters.getBluetoothDevice().getAddress() + "\".", connectException);

            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.w(LOG_TAG, "doInBackground, 50: Could not close socket with device \"" + parameters.getBluetoothDevice().getAddress() + "\".");
            }
            bluetoothSocket = null;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        return bluetoothSocket;
    }
}
