package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector;

import android.bluetooth.BluetoothSocket;

/**
 * The result returned once the {@link AsyncTaskConnectWithAudioRecorder} concludes its connection
 * attempt with the audio recorder.
 */
public class ConnectWithAudioRecorderResult {

    /**
     * The code returned from the connection attempt with the audio recorder.
     */
    private final int returnCode;

    /**
     * The socket which represents the connection between the audio recorder and the application.
     */
    private final BluetoothSocket bluetoothSocket;

    /**
     * Constructor.
     *
     * @param returnCode      The code returned from the connection attempt with the audio recorder.
     * @param bluetoothSocket The socket which represents the connection between the audio recorder and the application.
     */
    ConnectWithAudioRecorderResult(int returnCode, BluetoothSocket bluetoothSocket) {
        this.returnCode = returnCode;
        this.bluetoothSocket = bluetoothSocket;
    }

    /**
     * Returns the code returned from the connection attempt with the audio recorder.
     *
     * @return The code returned from the connection attempt with the audio recorder.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the socket which represents the connection between the audio recorder and the application.
     *
     * @return The socket which represents the connection between the audio recorder and the application.
     */
    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }
}
