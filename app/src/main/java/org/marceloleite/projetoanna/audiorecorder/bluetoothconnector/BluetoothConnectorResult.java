package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;

/**
 * Created by marcelo on 26/06/17.
 */

public class BluetoothConnectorResult {

    private int returnCode;

    private AudioRecorder audioRecorder;

    public BluetoothConnectorResult(int returnCode, AudioRecorder audioRecorder) {
        this.returnCode = returnCode;
        this.audioRecorder = audioRecorder;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }
}
