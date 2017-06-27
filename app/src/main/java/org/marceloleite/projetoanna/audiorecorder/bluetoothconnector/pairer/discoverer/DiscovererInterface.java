package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer;

/**
 * Establishes the method required to communicate with a {@link Discoverer} object.
 */
public interface DiscovererInterface {

    /**
     * Returns the parameters informed to build the {@link Discoverer} object.
     *
     * @return The parameters informed to build the {@link Discoverer} object.
     */
    DiscovererParameters getDiscovererParameters();

    /**
     * Informs the result of the bluetooth device discovering process.
     *
     * @param discoveringResult The result of the bluetooth device discovering process.
     */
    void discoveringResult(DiscoveringResult discoveringResult);
}
