package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.utils.Log;

import java.io.IOException;

/**
 * A asynchronous task which stablishes a bluetooth connection with a device.
 */
public class AsyncTaskConnectWithDevice extends AsyncTask<Void, Integer, BluetoothSocket> {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AsyncTaskConnectWithDevice.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The alert dialog to inform the user that a bluetooth connection is being established.
     */
    private AlertDialogConnectingToDevice alertDialogConnectingToDevice;

    /**
     * The parameters to execute the bluetooth connection asynchronous task.
     */
    private AsyncTaskConnectWithDeviceParameters parameters;

    /**
     * The object which should be executed after the bluetooth connection is concluded.
     */
    private AsyncTaskConnectWithDeviceResult result;

    public AsyncTaskConnectWithDevice(AsyncTaskConnectWithDeviceParameters asyncTaskConnectWithDeviceParameters, AsyncTaskConnectWithDeviceResult asyncTaskConnectWithDeviceResult) {
        this.parameters = asyncTaskConnectWithDeviceParameters;
        this.result = asyncTaskConnectWithDeviceResult;
    }

    @Override
    protected void onPreExecute() {
        alertDialogConnectingToDevice = new AlertDialogConnectingToDevice(parameters.getAppCompatActivity(), parameters.getBluetoothDevice(), parameters.getViewGroup());
        alertDialogConnectingToDevice.show();
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        alertDialogConnectingToDevice.dismiss();
        result.connectWithDeviceProcessFinished(bluetoothSocket);
    }

    @Override
    protected BluetoothSocket doInBackground(Void... voids) {

        try {
            BluetoothSocket bluetoothSocket = parameters.getBluetoothDevice().createRfcommSocketToServiceRecord(Bluetooth.BLUETOOTH_SERVICE_UUID);

            try {
                bluetoothSocket.connect();
            } catch (IOException connectException) {
                Log.e(LOG_TAG, "doInBackground (69): Exception while connecting: " + connectException.getMessage());
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.e(LOG_TAG, "doInBackground (73): Exception while closing connection: " + closeException.getMessage());
                }
                throw new RuntimeException("Could not connect with device \"" + parameters.getBluetoothDevice().getAddress() + "\".", connectException);
            }
            return bluetoothSocket;
        } catch (IOException ioException) {
            throw new RuntimeException("Exception while creating RFCOMM socket with service.", ioException);
        }
    }
}
