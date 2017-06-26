package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorReturnCodes;
import org.marceloleite.projetoanna.utils.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * A asynchronous task which stablishes a bluetooth connection with a device.
 */
public class AsyncTaskConnectWithAudioRecorder extends AsyncTask<Void, Integer, ConnectWithAudioRecorderResult> {

    /**
     * The UUID used to identify the audio recorder bluetooth service.
     */
    private static final UUID BLUETOOTH_SERVICE_UUID = UUID.fromString("f5934b96-0110-11e6-8d22-5e5517507c66");

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AsyncTaskConnectWithAudioRecorder.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The alert dialog to inform the user that a bluetooth connection is being established.
     */
    private AlertDialogConnectingWithAudioRecorder alertDialogConnectingWithAudioRecorder;

    /**
     * The object which should be executed after the bluetooth connection is concluded.
     */
    private ConnectWithAudioRecorderInterface connectWithAudioRecorderInterface;

    private ConnectWithAudioRecorderParameters connectWithAudioRecorderParameters;

    public AsyncTaskConnectWithAudioRecorder(ConnectWithAudioRecorderInterface connectWithAudioRecorderInterface) {
        this.connectWithAudioRecorderInterface = connectWithAudioRecorderInterface;
        this.connectWithAudioRecorderParameters = connectWithAudioRecorderInterface.getConnectWithAudioRecorderParameters();
    }

    @Override
    protected void onPreExecute() {
        ConnectWithAudioRecorderParameters connectWithAudioRecorderParameters = connectWithAudioRecorderInterface.getConnectWithAudioRecorderParameters();
        alertDialogConnectingWithAudioRecorder = new AlertDialogConnectingWithAudioRecorder(connectWithAudioRecorderParameters.getAppCompatActivity(), connectWithAudioRecorderParameters.getBluetoothDevice());
        alertDialogConnectingWithAudioRecorder.show();
    }

    @Override
    protected void onPostExecute(ConnectWithAudioRecorderResult connectWithAudioRecorderResult) {
        alertDialogConnectingWithAudioRecorder.dismiss();
        connectWithAudioRecorderInterface.connectWithAudioRecorderFinished(connectWithAudioRecorderResult);
    }

    @Override
    protected ConnectWithAudioRecorderResult doInBackground(Void... voids) {
        BluetoothDevice bluetoothDevice = connectWithAudioRecorderParameters.getBluetoothDevice();

        try {
            BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BLUETOOTH_SERVICE_UUID);

            try {
                bluetoothSocket.connect();
                return new ConnectWithAudioRecorderResult(BluetoothConnectorReturnCodes.SUCCESS, bluetoothSocket);
            } catch (IOException connectException) {
                Log.e(LOG_TAG, "doInBackground (69): Exception while connecting: " + connectException.getMessage());
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.e(LOG_TAG, "doInBackground (73): Exception while closing connection: " + closeException.getMessage());
                }
                return new ConnectWithAudioRecorderResult(BluetoothConnectorReturnCodes.CONNECTION_FAILED, null);
            }
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "doInBackground (85): Exception while creating RFCOMM socket with service: " + ioException.getMessage());
            return new ConnectWithAudioRecorderResult(BluetoothConnectorReturnCodes.CONNECTION_FAILED, null);
        }
    }
}
