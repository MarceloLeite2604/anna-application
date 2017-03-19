package org.marceloleite.projetoanna.bluetooth.pairer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;

/**
 * Created by Marcelo Leite on 27/04/2016.
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {

    private Pairer pairer;

    public BroadcastReceiver(Pairer pairer) {
        this.pairer = pairer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            Log.d(MainActivity.LOG_TAG, "onReceive, 25: Starting discovery.");
            pairer.discoverDevicesStarted();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.d(MainActivity.LOG_TAG, "onReceive, 29: Finishing discovery.");
            pairer.discoverDevicesFinished();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d(MainActivity.LOG_TAG, "onReceive, 33: Device \"" + bluetoothDevice.getName() + "\" found.");
            pairer.deviceFound(bluetoothDevice);
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
            if (previousState == BluetoothDevice.BOND_NONE && state == BluetoothDevice.BOND_BONDING) {
                Log.d(MainActivity.LOG_TAG, "onReceive, 39: Pairing started.");
                pairer.pairingStarted();
            } else if (previousState == BluetoothDevice.BOND_BONDING) {
                Log.d(MainActivity.LOG_TAG, "onReceive, 42: Pairing finished.");
                pairer.pairingFinished(state == BluetoothDevice.BOND_BONDED);
            }
        }
    }
}
