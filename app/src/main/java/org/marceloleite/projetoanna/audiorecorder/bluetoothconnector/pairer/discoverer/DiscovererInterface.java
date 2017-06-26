package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer;

/**
 * Created by marcelo on 26/06/17.
 */

public interface DiscovererInterface {

    DiscovererParameters getDiscovererParameters();

    void discoveringResult(DiscoveringResult discoveringResult);
}
