package org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 27/04/2016.
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = BroadcastReceiver.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(BroadcastReceiver.class);
    }

    private Pairer pairer;

    public BroadcastReceiver(Pairer pairer) {
        this.pairer = pairer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            Log.d(BroadcastReceiver.class, LOG_TAG, "onReceive (37): Starting discovery.");
            pairer.discoverDevicesStarted();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.d(BroadcastReceiver.class, LOG_TAG, "onReceive (40): Finishing discovery.");
            pairer.discoverDevicesFinished();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d(BroadcastReceiver.class, LOG_TAG, "onReceive (44): Device \"" + bluetoothDevice.getName() + "\" found.");
            pairer.deviceFound(bluetoothDevice);
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
            if (previousState == BluetoothDevice.BOND_NONE && state == BluetoothDevice.BOND_BONDING) {
                Log.d(BroadcastReceiver.class, LOG_TAG, "onReceive (50): Pairing started.");
                pairer.pairingStarted();
            } else if (previousState == BluetoothDevice.BOND_BONDING) {
                Log.d(BroadcastReceiver.class, LOG_TAG, "onReceive (53): Pairing finished.");
                pairer.pairingFinished(state == BluetoothDevice.BOND_BONDED);
            }
        }
    }
}
