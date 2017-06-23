package org.marceloleite.projetoanna.audiorecorder.bluetooth.connector;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import org.marceloleite.projetoanna.utils.Log;

/**
 * The parameters to the asynchronous task which established a bluetooth connection with another device.
 */
public class AsyncTaskConnectWithDeviceParameters {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AsyncTaskConnectWithDeviceParameters.class.getSimpleName();

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
     * The parent view group of the "establishing connection" view alert to be created.
     */
    private ViewGroup viewGroup;

    /**
     * Object constructor.
     *
     * @param bluetoothDevice   The bluetooth device which the connection must be established.
     * @param appCompatActivity The activity which requested the bluetooth connection.
     * @param viewGroup         The pared view group of the "establishing connection" view alert to be created.
     */
    public AsyncTaskConnectWithDeviceParameters(BluetoothDevice bluetoothDevice, AppCompatActivity appCompatActivity, ViewGroup viewGroup) {
        this.bluetoothDevice = bluetoothDevice;
        this.appCompatActivity = appCompatActivity;
        this.viewGroup = viewGroup;
    }

    BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    ViewGroup getViewGroup() {
        return viewGroup;
    }
}
