package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

/**
 * Created by marcelo on 26/06/17.
 */

public interface PairerInterface {

    PairerParameters getPairerParameters();

    void pairingResult(PairingResult pairingResult);
}
