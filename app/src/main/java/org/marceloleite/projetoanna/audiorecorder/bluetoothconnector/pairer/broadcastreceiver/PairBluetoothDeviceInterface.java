package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.broadcastreceiver;

/**
 * Created by marcelo on 26/06/17.
 */

public interface PairBluetoothDeviceInterface {

    void pairingStarted();

    void pairingFinished(boolean success);
}
