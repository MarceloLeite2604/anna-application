package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;

/**
 * The result returned from {@link BluetoothConnector} object after select, pair and connect with
 * the bluetooth device which represents the audio recorder.
 */
public class BluetoothConnectorResult {

    /**
     * The code returned from the connection attempt.
     */
    private final int returnCode;

    /**
     * The audio recorder connected with the application (if connection attempt was finished successfully).
     */
    private final AudioRecorder audioRecorder;

    /**
     * Constructor.
     *
     * @param returnCode    The code returned from the connection attempt.
     * @param audioRecorder The audio recorder connected with the application (if connection attempt was finished successfully).
     */
    BluetoothConnectorResult(int returnCode, AudioRecorder audioRecorder) {
        this.returnCode = returnCode;
        this.audioRecorder = audioRecorder;
    }

    /**
     * Returns the code returned from the connection attempt.
     *
     * @return The code returned from the connection attempt.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the audio recorder connected with the application (if connection attempt was finished successfully).
     *
     * @return The audio recorder connected with the application (if connection attempt was finished successfully).
     */
    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }
}
