package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.start;

/**
 * Establishes the parameters required to open the alert dialog requesting to start bluetooth
 * devices discovering and the method executed once the user selects an option.
 */
public interface StartDiscoveringInterface {

    /**
     * Returns the parameters required to open the alert dialog requesting to start bluetooth
     * devices discovering.
     *
     * @return The parameters required to open the alert dialog requesting to start bluetooth
     * devices discovering.
     */
    StartDiscoveringParameters getStartDiscoveryParameters();

    /**
     * Informs the option selected by the user on the alert dialog which requests the uses to start
     * bluetooth device discovering.
     *
     * @param optionSelected The option selected by the user.
     */
    void startDiscoveryResult(int optionSelected);
}
