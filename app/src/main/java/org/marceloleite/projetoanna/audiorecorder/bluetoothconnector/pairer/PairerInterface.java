package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

/**
 * Specifies the methods required by the {@link Pairer} object.
 */
public interface PairerInterface {

    /**
     * Returns the parameters informed to create the {@link Pairer} object.
     *
     * @return The parameters informed to create the {@link Pairer} object.
     */
    PairerParameters getPairerParameters();

    /**
     * Informs the result of the pairing process.
     *
     * @param pairingResult A {@link PairingResult} object with the result of the pairing process.
     */
    void pairingResult(PairingResult pairingResult);
}
