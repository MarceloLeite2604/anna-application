package org.marceloleite.projetoanna.bluetooth.pairer;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Marcelo Leite on 02/05/2016.
 */
public interface PairerInterface {

    BluetoothDevice getBluetoothDevice();

    void setDevicePaired(boolean devicePaired);

    void pairingFinished();
}
