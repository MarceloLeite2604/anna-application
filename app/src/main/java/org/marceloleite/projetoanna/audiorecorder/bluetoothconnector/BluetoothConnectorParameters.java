package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector;

import android.support.v7.app.AppCompatActivity;

/**
 * The parameters required to execute the bluetooth connection attempt process.
 */
public class BluetoothConnectorParameters {

    /**
     * The activity which requested the bluetooth connection.
     */
    private AppCompatActivity appCompatActivity;

    /**
     * Object constructor.
     *
     * @param appCompatActivity The activity which is requesting the bluetooth connection.
     */
    public BluetoothConnectorParameters(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * Returns the activity which requested the bluetooth connection.
     *
     * @return The activity which requested the bluetooth connection.
     */
    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

}
