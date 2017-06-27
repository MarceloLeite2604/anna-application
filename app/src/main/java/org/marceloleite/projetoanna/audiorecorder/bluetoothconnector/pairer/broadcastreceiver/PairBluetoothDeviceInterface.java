package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.broadcastreceiver;

/**
 * Specifies the methods required by the {@link BroadcastReceiverPairBluetoothDevice} object to
 * inform the pairing status received.
 */
public interface PairBluetoothDeviceInterface {

    /**
     * Method executed when the pairing with bluetooth device has started.
     */
    void pairingStarted();

    /**
     * Method executed when the pairing with bluetooth device has finished.
     *
     * @param success Indicates if the pairing with the bluetooth device was done successfully.
     */
    void pairingFinished(boolean success);
}
