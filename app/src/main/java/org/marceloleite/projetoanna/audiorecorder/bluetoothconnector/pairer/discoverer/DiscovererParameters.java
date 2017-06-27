package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer;

import android.support.v7.app.AppCompatActivity;

/**
 * The parameters required to construct a {@link Discoverer} object.
 */
public class DiscovererParameters {

    /**
     * The application being executed.
     */
    private final AppCompatActivity appCompatActivity;

    /**
     * Constructor.
     *
     * @param appCompatActivity The application being executed.
     */
    public DiscovererParameters(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * Returns the application being executed.
     *
     * @return The application being executed.
     */
    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }
}
