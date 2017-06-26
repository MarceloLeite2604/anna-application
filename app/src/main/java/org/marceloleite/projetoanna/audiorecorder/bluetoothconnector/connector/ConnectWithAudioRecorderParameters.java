package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.utils.Log;

/**
 * The parameters to the asynchronous task which established a bluetooth connection with another device.
 */
public class ConnectWithAudioRecorderParameters {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ConnectWithAudioRecorderParameters.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The bluetooth device which the connection must be established.
     */
    private BluetoothDevice bluetoothDevice;

    /**
     * The activity which requested the bluetooth connection.
     */
    private AppCompatActivity appCompatActivity;

    /**
     * Object constructor.
     *
     * @param bluetoothDevice   The bluetooth device which the connection must be established.
     * @param appCompatActivity The activity which requested the bluetooth connection.
     */
    public ConnectWithAudioRecorderParameters(BluetoothDevice bluetoothDevice, AppCompatActivity appCompatActivity) {
        this.bluetoothDevice = bluetoothDevice;
        this.appCompatActivity = appCompatActivity;
    }

    BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }
}
