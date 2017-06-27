package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.broadcastreceiver;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A {@link android.content.BroadcastReceiver} to control the pairing states received when Android
 * is pairing with a bluetooth device.
 */
public class BroadcastReceiverPairBluetoothDevice extends android.content.BroadcastReceiver {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = BroadcastReceiverPairBluetoothDevice.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The object which contains the methods to be executed when the pairing state changes.
     */
    private PairBluetoothDeviceInterface pairBluetoothDeviceInterface;

    /**
     * Constructor.
     *
     * @param pairBluetoothDeviceInterface The object which contains the methods to be executed
     *                                     when the pairing state changes.
     */
    public BroadcastReceiverPairBluetoothDevice(PairBluetoothDeviceInterface pairBluetoothDeviceInterface) {
        this.pairBluetoothDeviceInterface = pairBluetoothDeviceInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
            if (previousState == BluetoothDevice.BOND_NONE && state == BluetoothDevice.BOND_BONDING) {
                Log.d(LOG_TAG, "onReceive (50): Pairing started.");
                pairBluetoothDeviceInterface.pairingStarted();
            } else if (previousState == BluetoothDevice.BOND_BONDING) {
                Log.d(LOG_TAG, "onReceive (53): Pairing finished.");
                pairBluetoothDeviceInterface.pairingFinished(state == BluetoothDevice.BOND_BONDED);
            }
        }
    }
}
