package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.broadcastreceiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A {@link android.content.BroadcastReceiver} to control the messages from Android when a bluetooth
 * device is found through discovering process.
 */
public class BroadcastReceiverDiscoverBluetoothDevices extends android.content.BroadcastReceiver {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = BroadcastReceiverDiscoverBluetoothDevices.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The object which contains the methods to be executed when a bluetooth device is discovered.
     */
    private DiscoverBluetoothDevicesInterface discoverBluetoothDevicesInterface;

    /**
     * Constructor.
     *
     * @param discoverBluetoothDevicesInterface The object which contains the methods to be executed when a bluetooth device is discovered.
     */
    public BroadcastReceiverDiscoverBluetoothDevices(DiscoverBluetoothDevicesInterface discoverBluetoothDevicesInterface) {
        this.discoverBluetoothDevicesInterface = discoverBluetoothDevicesInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            Log.d(LOG_TAG, "onReceive (37): Starting discovery.");
            discoverBluetoothDevicesInterface.discoverDevicesStarted();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.d(LOG_TAG, "onReceive (40): Finishing discovery.");
            discoverBluetoothDevicesInterface.discoverDevicesFinished();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d(LOG_TAG, "onReceive (44): Device \"" + bluetoothDevice.getName() + "\" found.");
            discoverBluetoothDevicesInterface.deviceFound(bluetoothDevice);
        }
    }
}
