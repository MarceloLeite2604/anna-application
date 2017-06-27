package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector;

import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorderInterface;

/**
 * The parameters required to execute the bluetooth connection attempt process.
 */
public class BluetoothConnectorParameters {

    /**
     * The activity which requested the bluetooth connection.
     */
    private final AppCompatActivity appCompatActivity;

    private final AudioRecorderInterface audioRecorderInterface;

    /**
     * Object constructor.
     *
     * @param appCompatActivity The activity which is requesting the bluetooth connection.
     */
    public BluetoothConnectorParameters(AppCompatActivity appCompatActivity, AudioRecorderInterface audioRecorderInterface) {
        this.appCompatActivity = appCompatActivity;
        this.audioRecorderInterface = audioRecorderInterface;
    }

    /**
     * Returns the activity which requested the bluetooth connection.
     *
     * @return The activity which requested the bluetooth connection.
     */
    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    public AudioRecorderInterface getAudioRecorderInterface() {
        return audioRecorderInterface;
    }
}
